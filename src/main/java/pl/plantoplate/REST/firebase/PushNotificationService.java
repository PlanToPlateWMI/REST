/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.
 */

package pl.plantoplate.REST.firebase;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Firebase notification service to send messages
 */
@Service
@Slf4j
public class PushNotificationService {

    public void send(String targetToken, String title) {

        try {
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

        } catch (Exception e) {
            log.error("Cannot send push notification");
        }

    }


    public void sendAll(List<String> targetTokens, String title) {

        try {
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
        } catch (Exception e) {
            log.error("Cannot send push notification");
        }
    }
}