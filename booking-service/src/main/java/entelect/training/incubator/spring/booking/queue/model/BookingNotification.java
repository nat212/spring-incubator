package entelect.training.incubator.spring.booking.queue.model;

import lombok.Data;

@Data
public class BookingNotification {
    private String phoneNumber;
    private String message;
}
