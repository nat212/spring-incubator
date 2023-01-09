package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.exception.CustomerNotFoundException;
import entelect.training.incubator.spring.booking.exception.FlightNotFoundException;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.BookingSearchRequest;
import entelect.training.incubator.spring.booking.service.BookingService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("bookings")
public class BookingsController {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingsController.class);

    private final BookingService bookingService;

    public BookingsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        LOGGER.info("Processing booking creation for booking={}", booking);

        try {
            final Booking savedBooking = bookingService.createBooking(booking);
            LOGGER.trace("Booking created");
            return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
        } catch (CustomerNotFoundException ex) {
            LOGGER.trace("Customer not found");
            return ResponseEntity.badRequest().body("Customer not found");
        } catch (FlightNotFoundException ex) {
            LOGGER.trace("Flight not found");
            return ResponseEntity.badRequest().body("Flight not found");
        }
    }

    @GetMapping()
    public ResponseEntity<?> getBookings() {
        LOGGER.info("Fetching all bookings");
        List<Booking> bookings = bookingService.getBookings();

        if (!bookings.isEmpty()) {
            LOGGER.trace("Found bookings");
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        }

        LOGGER.trace("No bookings found");
        return ResponseEntity.notFound().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        LOGGER.info("Processing booking search request for booking id={}", id);
        Booking booking = bookingService.getBooking(id);

        if (booking != null) {
            LOGGER.trace("Found booking");
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }

        LOGGER.trace("Booking not found");
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchBookings(@RequestBody BookingSearchRequest searchRequest) {
        LOGGER.info("Processing booking search request: {}", searchRequest);
        List<Booking> bookings = bookingService.searchBookings(searchRequest);

        if (!bookings.isEmpty()) {
            LOGGER.trace("Found bookings: {}", bookings);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        }

        LOGGER.trace("No bookings found");
        return ResponseEntity.notFound().build();
    }
}
