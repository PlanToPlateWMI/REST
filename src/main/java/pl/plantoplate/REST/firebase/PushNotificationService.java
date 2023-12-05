package pl.plantoplate.REST.firebase;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PushNotificationService {

    public void send(String targetToken, String title) {

        Notification notification = Notification.builder()
                .setTitle(title)
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(targetToken)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendAll(List<String> targetTokens, String title) {

        Notification notification = Notification.builder()
                .setTitle(title)
                .build();

        List<Message> messages = new ArrayList<>();
        for (String token : targetTokens) {
            messages.add(Message.builder()
                    .setNotification(notification)
                    .setToken(token)
                    .build());
        }

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendAll(messages);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}