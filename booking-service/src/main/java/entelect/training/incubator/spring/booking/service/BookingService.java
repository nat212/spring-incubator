package entelect.training.incubator.spring.booking.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entelect.training.incubator.spring.booking.client.RewardsClient;
import entelect.training.incubator.spring.booking.exception.CustomerNotFoundException;
import entelect.training.incubator.spring.booking.exception.FlightNotFoundException;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.BookingSearchRequest;
import entelect.training.incubator.spring.booking.model.Customer;
import entelect.training.incubator.spring.booking.model.Flight;
import entelect.training.incubator.spring.booking.queue.MessageCreator;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Service
public class BookingService {
    @Value("${flights.apiUrl}")
    private String flightsApiUrl;

    @Value("${customers.apiUrl}")
    private String customersApiUrl;

    @Value("${flights.credentials.username}")
    private String flightsUsername;

    @Value("${flights.credentials.password}")
    private String flightsPassword;

    @Value("${customers.credentials.username}")
    private String customersUsername;

    @Value("${customers.credentials.password}")
    private String customersPassword;

    private final BookingRepository bookingRepository;

    private final RewardsClient rewardsClient;

    private final ObjectMapper objectMapper;

    @Autowired
    private final MessageCreator messageCreator;

    public BookingService(BookingRepository bookingRepository, RewardsClient rewardsClient, ObjectMapper objectMapper, MessageCreator messageCreator) {
        this.bookingRepository = bookingRepository;
        this.rewardsClient = rewardsClient;
        this.objectMapper = objectMapper;
        this.messageCreator = messageCreator;
    }

    public Booking createBooking(Booking booking) throws CustomerNotFoundException, FlightNotFoundException {
        Customer customer = getCustomer(booking.getCustomerId());
        Flight flight = getFlight(booking.getFlightId());
        String referenceNumber = randomAlphabetic(3).toUpperCase() + randomNumeric(4);
        booking.setReferenceNumber(referenceNumber);
        booking = bookingRepository.save(booking);
        // Update rewards service.
        rewardsClient.captureRewardsRequest(customer.getPassportNumber(), flight.getSeatCost());
        String message = String.format("Molo Air: Confirming flight %s booked for %s on %s", flight.getFlightNumber(), customer.getFullName(), flight.getDepartureTime().toString());
        messageCreator.sendMessage(customer.getPhoneNumber(), message);
        return booking;
    }

    private Customer getCustomer(Integer customerId) throws CustomerNotFoundException {
        String url = customersApiUrl + "/{id}";
        String authString = customersUsername + ":" + customersPassword;
        String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<?> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<JsonNode> response = new RestTemplate().exchange(url, HttpMethod.GET, request, JsonNode.class, customerId);
            if (response.getStatusCode().isError()) {
                throw new CustomerNotFoundException();
            }
            return objectMapper.convertValue(response.getBody(), Customer.class);
        } catch (HttpClientErrorException | NullPointerException ex) {
            throw new CustomerNotFoundException();
        }
    }

    private Flight getFlight(Integer flightId) throws FlightNotFoundException {
        String url = flightsApiUrl + "/{id}";
        String authString = flightsUsername + ":" + flightsPassword;
        String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<?> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<JsonNode> response = new RestTemplate().exchange(url, HttpMethod.GET, request, JsonNode.class, flightId);
            if (response.getStatusCode().isError()) {
                throw new FlightNotFoundException();
            }
            return objectMapper.convertValue(response.getBody(), Flight.class);
        } catch (HttpClientErrorException | NullPointerException ex) {
            throw new FlightNotFoundException();
        }
    }

    public List<Booking> getBookings() {
        Iterable<Booking> bookingIterable = bookingRepository.findAll();

        List<Booking> result = new ArrayList<>();
        bookingIterable.forEach(result::add);
        return result;
    }

    public Booking getBooking(Integer id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public List<Booking> searchBookings(BookingSearchRequest searchRequest) {
        if (searchRequest.getReferenceNumber() != null) {
            return getBookingByReferenceNumber(searchRequest.getReferenceNumber());
        }
        return getBookingsByCustomerId(searchRequest.getCustomerId());
    }

    private List<Booking> getBookingsByCustomerId(Integer customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    private List<Booking> getBookingByReferenceNumber(String referenceNumber) {
        return bookingRepository.findByReferenceNumber(referenceNumber);
    }
}
