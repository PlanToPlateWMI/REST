package pl.plantoplate.REST.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("Mail Sender Test")
class MailSenderServiceImplTest {


    private MailSenderServiceImpl mailSenderService;
    private JavaMailSender mailSender;


    @BeforeEach
    public void setUp(){
        mailSender = mock(JavaMailSender.class);
        mailSenderService = new MailSenderServiceImpl(mailSender);
    }

    @Test
    @DisplayName("email send to correct address with correct code")
    void shouldSendEmailToCorrectEmailAndWithCorrectCode(){

        //given
        int code = 1234;
        String emailTo = "test@gmail.com";
        MailParams mailParams = new MailParams(code, emailTo);

        //when
        mailSenderService.send(mailParams, EmailType.registration);

        //then
        ArgumentCaptor<SimpleMailMessage> messageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageArgumentCaptor.capture());

        SimpleMailMessage message = messageArgumentCaptor.getValue();

        assertEquals(message.getTo()[0], emailTo);
        assertTrue(message.getText().contains(String.valueOf(code)));
    }
}
