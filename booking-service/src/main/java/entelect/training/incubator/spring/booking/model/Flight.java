package entelect.training.incubator.spring.booking.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Flight {
    private Integer id;
    private String flightNumber;
    private BigDecimal seatCost;
    private LocalDateTime departureTime;
}
