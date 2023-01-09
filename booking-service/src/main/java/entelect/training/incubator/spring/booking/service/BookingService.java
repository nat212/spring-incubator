package entelect.training.incubator.spring.booking.service;


import entelect.training.incubator.spring.booking.exception.CustomerNotFoundException;
import entelect.training.incubator.spring.booking.exception.FlightNotFoundException;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.BookingSearchRequest;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
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

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Booking booking) throws CustomerNotFoundException, FlightNotFoundException {
        getCustomer(booking.getCustomerId());
        getFlight(booking.getFlightId());
        String referenceNumber = randomAlphabetic(3).toUpperCase() + randomNumeric(4);
        booking.setReferenceNumber(referenceNumber);
        return bookingRepository.save(booking);
    }

    private void getCustomer(Integer customerId) throws CustomerNotFoundException {
        String url = customersApiUrl + "/{id}";
        String authString = customersUsername + ":" + customersPassword;
        String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<?> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<?> response = new RestTemplate().exchange(url, HttpMethod.GET, request, Object.class, customerId);
            if (response.getStatusCode().isError()) {
                throw new CustomerNotFoundException();
            }
        } catch (HttpClientErrorException ex) {
            throw new CustomerNotFoundException();
        }
    }

    private void getFlight(Integer flightId) throws FlightNotFoundException {
        String url = flightsApiUrl + "/{id}";
        String authString = flightsUsername + ":" + flightsPassword;
        String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<?> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<?> response = new RestTemplate().exchange(url, HttpMethod.GET, request, Object.class, flightId);
            if (response.getStatusCode().isError()) {
                throw new FlightNotFoundException();
            }
        } catch (HttpClientErrorException ex) {
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
