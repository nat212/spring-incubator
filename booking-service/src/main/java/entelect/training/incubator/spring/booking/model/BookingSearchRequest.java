package entelect.training.incubator.spring.booking.model;

import lombok.Data;

@Data
public class BookingSearchRequest {
    private String referenceNumber;
    private Integer customerId;
}
