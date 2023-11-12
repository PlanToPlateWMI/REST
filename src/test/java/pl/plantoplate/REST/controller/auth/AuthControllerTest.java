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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.controller.dto.request.EmailPasswordRequest;
import pl.plantoplate.REST.controller.dto.request.SignupRequest;
import pl.plantoplate.REST.controller.dto.response.CodeResponse;
import pl.plantoplate.REST.controller.dto.response.JwtResponse;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.mail.EmailType;
import pl.plantoplate.REST.mail.MailParams;
import pl.plantoplate.REST.mail.MailSenderService;
import pl.plantoplate.REST.repository.UserRepository;
import pl.plantoplate.REST.security.JwtUtils;
import pl.plantoplate.REST.service.CategoryService;
import pl.plantoplate.REST.service.GroupService;
import pl.plantoplate.REST.service.UserService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test endpoints of AuthController - http code and responses
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("AuthController test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtUtils utils;

    @Autowired
    private PasswordEncoder encoder;

    @MockBean
    private UserService userService;
    @MockBean
    private MailSenderService mailSenderService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CategoryService categoryService;

    @SpyBean
    private GroupService groupService;

    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Register User with valid email")
    void shouldEnableRegistrationWhenUserWithEmailNotExistsAndSaveUserInDBandSendEmail() throws Exception {

        //get
        String email = "test@gmail.com";
        String username = "username";
        String password = "password";
        when(userService.existsByEmailAndActiveTrue(email)).thenReturn(false);
        SignupRequest user = new SignupRequest(email, password, username);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        verify(userService).existsByEmailAndActiveTrue(email);


        //check if code sent to email is equals code from response
        ArgumentCaptor<MailParams> mailParamsArgumentCaptor = ArgumentCaptor.forClass(MailParams.class);
        verify(mailSenderService).send(mailParamsArgumentCaptor.capture(), any(EmailType.class));

        MailParams mailParams = mailParamsArgumentCaptor.getValue();
        CodeResponse codeResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), CodeResponse.class);

        assertEquals(codeResponse.getCode(), mailParams.getCode());
        assertEquals(email, mailParams.getEmailTo());

    }


    @Test
    @DisplayName("Return conflict status if user with email exists")
    void shouldReturnStatusConflictIfUserWithEmailExists() throws Exception {

        //get
        String email = "test@gmail.com";
        String username = "username";
        String password = "password";
        when(userService.existsByEmailAndActiveTrue(email)).thenReturn(true);
        SignupRequest user = new SignupRequest(email, password, username);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        //then
        verify(userService).existsByEmailAndActiveTrue(email);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(mailSenderService);
    }


    @Test
    @DisplayName("Sign in activated user with correct email and password and return valid token")
    void shouldEnableActivatedUserToSigInIfUserWithEmailExistsAndReturnValidToken() throws Exception {

        //get
        String email = "test@gmail.com";
        String password = "password";
        String username = "username";
        when(userService.existsByEmail(email)).thenReturn(true);
        EmailPasswordRequest emailPasswordRequest = new EmailPasswordRequest(email, password);

        User user = new User(username, encoder.encode(password), email);
        user.setActive(true);
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(mapper.writeValueAsString(emailPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();


        //then
        String jwtFromResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), JwtResponse.class).getToken();
        assertTrue(utils.isJwtTokenValid(jwtFromResponse));

    }

    @Test
    @DisplayName("Return Forbidden if user not activated")
    void shouldReturnForbiddenIfNotActivatedUserTryToLogin() throws Exception {

        //get
        String email = "test@gmail.com";
        String password = "password";
        String username = "username";
        when(userService.existsByEmail(email)).thenReturn(true);
        EmailPasswordRequest emailPasswordRequest = new EmailPasswordRequest(email, password);

        User user = new User(username, encoder.encode(password), email);
        user.setActive(false); // user is not active
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(mapper.writeValueAsString(emailPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }


    @Test
    @DisplayName("Return BadRequest if user with email not exists")
    void shouldReturnBadRequestIfUserWithEmailNotExists() throws Exception{

        //get
        String email = "test@gmail.com";
        String password = "password";
        when(userService.existsByEmail(email)).thenReturn(false);
        EmailPasswordRequest emailPasswordRequest = new EmailPasswordRequest(email, password);


        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(mapper.writeValueAsString(emailPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Create group for valid user")
    void shouldCreateGroupForValidUserAndReturnValidToken() throws Exception{

        //get
        Category category = new Category(1,"Inne", new ArrayList<>());
        String email = "test@gmail.com";
        String password = "password";
        String username = "username";
        when(userService.existsByEmail(email)).thenReturn(true);
        when(categoryService.findByName("Inne")).thenReturn(category);

        EmailPasswordRequest emailPasswordRequest = new EmailPasswordRequest(email, password);

        User user = new User(username, encoder.encode(password), email);
        user.setActive(true);
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/group")
                .content(mapper.writeValueAsString(emailPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String jwtToken = mapper.readValue(mvcResult.getResponse().getContentAsString(), JwtResponse.class).getToken();
        assertTrue(utils.isJwtTokenValid(jwtToken));
        verify(groupService).createGroupAndAddAdmin(email);

    }



    @Test
    @DisplayName("BadRequest if user with email not exists")
    void shouldReturnBadRequestIfUserWithEmailNotExistsAndNotCreateGroup() throws Exception{

        //get
        String email = "test@gmail.com";
        String password = "password";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        EmailPasswordRequest emailPasswordRequest = new EmailPasswordRequest(email, password);


        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/group")
                .content(mapper.writeValueAsString(emailPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("BadRequest if user with email not exists")
    void shouldReturnBadRequestIfUserWithEmailNotExist() throws Exception{

        //get
        String email = "test@gmail.com";
        String password = "password";

        doThrow(EntityNotFound.class).when(userService).resetPassword(anyString(), anyString());

        EmailPasswordRequest emailPasswordRequest = new EmailPasswordRequest(email, password);


        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password/reset")
                .content(mapper.writeValueAsString(emailPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Update password")
    void shouldUpdatePassword() throws Exception{
        //get
        String email = "test@gmail.com";
        String password = "password";
        EmailPasswordRequest emailPasswordRequest = new EmailPasswordRequest(email, password);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password/reset")
                .content(mapper.writeValueAsString(emailPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(userService).resetPassword(anyString(), anyString());

    }


}
