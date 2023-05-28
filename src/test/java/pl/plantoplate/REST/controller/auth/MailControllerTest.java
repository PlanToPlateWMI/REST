package pl.plantoplate.REST.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.mail.EmailType;
import pl.plantoplate.REST.mail.MailParams;
import pl.plantoplate.REST.mail.MailSenderService;
import pl.plantoplate.REST.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MailController Test")
public class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    @MockBean
    private MailSenderService mailSenderService;

    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Return BadRequest if user with email not exists")
    void shouldReturnBadRequestIfUserWithEmailNotExists() throws Exception{
        //given
        String email = "test@gamil.com";
        when(userService.existsByEmail(email)).thenReturn(false);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/mail/code?mail="+email))
                .andExpect(status().isBadRequest());

        //then
        verifyNoInteractions(mailSenderService);
    }

    @Test
    @DisplayName("Generate activation code for valid user and sent it in response")
    void shouldSendActivationCodeForUserWithEmailThatExists() throws Exception{

        //given
        String email = "test@gamil.com";
        when(userService.existsByEmail(email)).thenReturn(true);

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/mail/code?email="+email))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ArgumentCaptor<MailParams> mailParamsArgumentCaptor = ArgumentCaptor.forClass(MailParams.class);
        verify(mailSenderService).send(mailParamsArgumentCaptor.capture(), any(EmailType.class));

        MailParams mailParams = mailParamsArgumentCaptor.getValue();
        CodeResponse codeResponse = mapper.readValue(result.getResponse().getContentAsString(), CodeResponse.class);

        assertEquals(codeResponse.getCode(), mailParams.getCode());
        assertEquals(email, mailParams.getEmailTo());


    }


}
