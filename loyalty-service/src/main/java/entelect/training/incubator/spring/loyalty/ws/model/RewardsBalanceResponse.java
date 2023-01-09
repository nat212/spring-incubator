package entelect.training.incubator.spring.loyalty.ws.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RewardsBalanceResponse {
    private BigDecimal balance;
}
