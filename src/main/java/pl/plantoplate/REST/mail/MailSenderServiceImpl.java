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
package pl.plantoplate.REST.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailSenderServiceImpl implements MailSenderService{

    @Value("${spring.mail.username}")
    private String emailFrom;

    private final JavaMailSender mailSender;

    @Autowired
    public MailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send email to email address from MailParams with code
     * @param mailParams
     */
    @Override
    public void send(MailParams mailParams) {
        String mailSubject = "Activate account in PlanToPlate mobile app";
        String messageBody = String.format("To confirm your email address use code :\n%d", mailParams.getCode());
        String emailTo = mailParams.getEmailTo();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject(mailSubject);
        message.setText(messageBody);

        mailSender.send(message);

        log.info("Code ["+ mailParams.getCode() +"] was sent to email [" + mailParams.getEmailTo()+"]");
    }

}
