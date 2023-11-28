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

/**
 * Implementation of MailSenderService {@link pl.plantoplate.REST.mail.MailSenderService} interface
 */
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
     * Send email to email address with code from MailParams and depends on type email change subject and body of email
     * @param mailParams
     * @param emailType email type to send - for registration on reset password
     */
    @Override
    public void send(MailParams mailParams,  EmailType emailType) {
        String mailSubject = null;
        String messageBody = null;
        if (emailType.equals(EmailType.registration)) {
             mailSubject = "Activate account in PlanToPlate mobile app";
             messageBody = String.format("To confirm your email address to continue registration use code :\n%d\n" +
                     "\n\n\n" +
                     "Żeby potwierdzić adres mailowy użyj poniższego kodu żeby kontynuować rejestrację :\n%d", mailParams.getCode(), mailParams.getCode());
        }else if(emailType.equals(EmailType.reset)){
            mailSubject = "Reset password to your account in PlanToPlate mobile app";
            messageBody = String.format("To confirm your email address to reset password use code :\n%d\n" +
                    "\n\n\n" +
                    "Żeby potwierdzić adres mailowy do resetowania hasła użyj poniższego kodu :\n%d", mailParams.getCode(), mailParams.getCode());
        }
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
