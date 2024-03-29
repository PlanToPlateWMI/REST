package pl.plantoplate.REST.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.controller.dto.request.EmailPasswordRequest;
import pl.plantoplate.REST.controller.dto.request.EmailRoleRequest;
import pl.plantoplate.REST.controller.dto.request.UsernameRequest;
import pl.plantoplate.REST.controller.dto.response.UsernameRoleEmailResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.repository.UserRepository;
import pl.plantoplate.REST.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    private static final String email = "email@gmail.com";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PasswordEncoder encoder;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }


    @Test
    void shouldReturnConflictIfUserExists() throws Exception {
        String email = "email";

        when(userService.existsByEmailAndActiveTrue(email)).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.
                get("/api/users/emails?email=" + email))
                .andExpect(status().isConflict());
    }


    @Test
    void shouldReturnOkIfUserNotExists() throws Exception {
        String email = "email";

        when(userService.existsByEmailAndActiveTrue(email)).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.
                get("/api/users/emails?email=" + email))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(value = email)
    void shouldUpdateUsername() throws Exception {
        String newUsername = "newUsername";

        User user = new User();
        user.setUsername(newUsername);
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);

        UsernameRequest usernameRequest = new UsernameRequest(newUsername);

        when(userService.updateUsername(email, usernameRequest.getUsername())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/username")
                .content(mapper.writeValueAsString(usernameRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(value = email)
    void shouldMatchPassword() throws Exception {
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userService.isPasswordMatch(email, password)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/password/match?password=" + password))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(value = email)
    void shouldNotMatchPassword() throws Exception {
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userService.isPasswordMatch(email, password)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/password/match?password=" + password))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(value = email, password = "password")
    void shouldUpdateEmail() throws Exception {
        String password = "password";
        String newEmail = "newEmail@gmail.com";
        User user = new User();
        user.setEmail(newEmail);
        user.setPassword(encoder.encode(password));
        user.setActive(true);
        user.setRole(Role.ROLE_ADMIN);

        when(userRepository.findByEmail(newEmail)).thenReturn(java.util.Optional.of(user));

        EmailPasswordRequest emailPasswordRequest = new EmailPasswordRequest(newEmail, password);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/email")
                .content(mapper.writeValueAsString(emailPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(value = email, roles = "ADMIN")
    void shouldUpdateRoles() throws Exception {

        String emailOfUserFromGroup = "test2@gmail.com";
        List<EmailRoleRequest> emailRoleRequestList = new ArrayList<>();
        EmailRoleRequest request = new EmailRoleRequest("ADMIN", emailOfUserFromGroup);
        emailRoleRequestList.add(request);

        User user = new User();
        user.setRole(Role.ROLE_ADMIN);
        user.setUsername("username");
        user.setEmail(emailOfUserFromGroup);

        when(userService.updateRoles(email, emailRoleRequestList)).thenReturn(List.of(user));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/roles")
                .content(mapper.writeValueAsString(emailRoleRequestList))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @WithMockUser(value = email)
    @Test
    void shouldReturnInfoOfAuthenticatedUser() throws Exception {
        String username = "username";
        Role role = Role.ROLE_ADMIN;
        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        user.setUsername(username);

        when(userService.findByEmail(email)).thenReturn(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isOk()).andReturn();

        UsernameRoleEmailResponse response = mapper.readValue(result.getResponse().getContentAsString(), UsernameRoleEmailResponse.class);
        assertEquals(username, response.getUsername());
        assertEquals(email, response.getEmail());
        assertEquals(role.name(), response.getRole());
    }


    @Test
    @WithMockUser(value = email)
    void shouldReturnListOfUsersOfTheSameGroup() throws Exception {
        //given
        String username = "username";
        Role role = Role.ROLE_ADMIN;

        Group group = new Group();

        User user = new User();
        user.setUsername(username);
        user.setRole(role);
        user.setEmail(email);
        user.setUserGroup(group);

        User user1 = new User();
        user1.setUsername(username);
        user1.setRole(role);
        user1.setEmail(email);
        user1.setUserGroup(group);

        group.setUsers(List.of(user, user1));

        List<User> usersOfTheSameGroup = List.of(user, user1);

        when(userService.getUserOfTheSameGroup(email)).thenReturn(usersOfTheSameGroup);

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/infos"))
                .andExpect(status().isOk()).andReturn();

        //then
        List<UsernameRoleEmailResponse> response = mapper.readValue(result.getResponse().getContentAsString(),new TypeReference<List<UsernameRoleEmailResponse>>(){});
        assertEquals(2, response.size());
    }




}
