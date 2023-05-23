package pl.plantoplate.REST.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

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
}
