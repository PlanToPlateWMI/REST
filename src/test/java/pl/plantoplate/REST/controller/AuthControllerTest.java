package pl.plantoplate.REST.controller;

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
import pl.plantoplate.REST.dto.Request.LoginRequest;
import pl.plantoplate.REST.dto.Request.SignupRequest;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.dto.Response.JwtResponse;
import pl.plantoplate.REST.entity.Role;
import pl.plantoplate.REST.entity.User;
import pl.plantoplate.REST.mail.MailParams;
import pl.plantoplate.REST.mail.MailSenderService;
import pl.plantoplate.REST.repository.UserRepository;
import pl.plantoplate.REST.security.JwtUtils;
import pl.plantoplate.REST.service.GroupService;
import pl.plantoplate.REST.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test endpoints of AuthController - http code and responses
 */
@SpringBootTest
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
        when(userService.existsByEmail(email)).thenReturn(false);
        SignupRequest user = new SignupRequest(email, password, username);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(savedUser.getRole(), Role.ROLE_USER);
        assertEquals(savedUser.getEmail(), email);
        assertEquals(savedUser.getUsername(), username);
        assertFalse(savedUser.isActive());


        //check if code sent to email is equals code from response
        ArgumentCaptor<MailParams> mailParamsArgumentCaptor = ArgumentCaptor.forClass(MailParams.class);
        verify(mailSenderService).send(mailParamsArgumentCaptor.capture());

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
        when(userService.existsByEmail(email)).thenReturn(true);
        SignupRequest user = new SignupRequest(email, password, username);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        //then
        verify(userService).existsByEmail(email);
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
        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = new User(username, encoder.encode(password), email);
        user.setActive(true);
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(mapper.writeValueAsString(loginRequest))
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
        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = new User(username, encoder.encode(password), email);
        user.setActive(false); // user is not active
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(mapper.writeValueAsString(loginRequest))
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
        LoginRequest loginRequest = new LoginRequest(email, password);


        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(mapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Create group for valid user")
    void shouldCreateGroupForValidUserAndReturnValidToken() throws Exception{

        //get
        String email = "test@gmail.com";
        String password = "password";
        String username = "username";
        when(userService.existsByEmail(email)).thenReturn(true);
        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = new User(username, encoder.encode(password), email);
        user.setActive(true);
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/group")
                .content(mapper.writeValueAsString(loginRequest))
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
        LoginRequest loginRequest = new LoginRequest(email, password);


        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/group")
                .content(mapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
