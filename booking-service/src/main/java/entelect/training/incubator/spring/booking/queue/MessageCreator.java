package entelect.training.incubator.spring.booking.queue;

import com.google.gson.Gson;
import entelect.training.incubator.spring.booking.queue.model.BookingNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageCreator {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void sendMessage(String phoneNumber, String message) {
        BookingNotification notification = new BookingNotification();
        notification.setPhoneNumber(phoneNumber);
        notification.setMessage(message);
        this.jmsMessagingTemplate.convertAndSend("inbound.queue", new Gson().toJson(notification));
    }
}
