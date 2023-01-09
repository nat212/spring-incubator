package entelect.training.incubator.spring.loyalty.ws.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CaptureRewardsRequest {
    private String passportNumber;
    private BigDecimal amount;
}
