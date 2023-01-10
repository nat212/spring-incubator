package entelect.training.incubator.spring.booking.client;

import entelect.training.incubator.spring.loyalty.ws.model.CaptureRewardsRequest;
import entelect.training.incubator.spring.loyalty.ws.model.CaptureRewardsResponse;
import entelect.training.incubator.spring.loyalty.ws.model.RewardsBalanceRequest;
import entelect.training.incubator.spring.loyalty.ws.model.RewardsBalanceResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.math.BigDecimal;

public class RewardsClient extends WebServiceGatewaySupport {

    public RewardsBalanceResponse rewardsBalanceRequest(String passportNumber) {
        RewardsBalanceRequest request = new RewardsBalanceRequest();
        request.setPassportNumber(passportNumber);

        return (RewardsBalanceResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
    }

    public CaptureRewardsResponse captureRewardsRequest(String passportNumber, BigDecimal amount) {
        CaptureRewardsRequest request = new CaptureRewardsRequest();
        request.setAmount(amount);
        request.setPassportNumber(passportNumber);

        return (CaptureRewardsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
    }
}
