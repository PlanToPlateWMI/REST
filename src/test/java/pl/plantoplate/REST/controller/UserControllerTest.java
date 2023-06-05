package pl.plantoplate.REST.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.dto.Request.UsernameRequest;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    private static final String email = "email@gmail.com";

    private static final ObjectMapper mapper = new ObjectMapper();

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
}
