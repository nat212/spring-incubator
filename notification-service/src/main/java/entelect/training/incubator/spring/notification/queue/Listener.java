package entelect.training.incubator.spring.notification.queue;

import com.google.gson.Gson;
import entelect.training.incubator.spring.notification.sms.client.SmsClient;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Map;

@Component
public class Listener {
    private final SmsClient smsClient;

    public Listener(SmsClient smsClient) {
        this.smsClient = smsClient;
    }

    @JmsListener(destination = "inbound.queue")
    public void receiveMessage(final Message jsonMessage) {
        if (jsonMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) jsonMessage;
            try {
                String messageData = textMessage.getText();
                Map<?, ?> map = new Gson().fromJson(messageData, Map.class);
                String phoneNumber = (String) map.get("phoneNumber");
                String message = (String) map.get("message");
                sendSms(phoneNumber, message);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendSms(String phoneNumber, String message) {
        smsClient.sendSms(phoneNumber, message);
    }
}
