package pl.plantoplate.REST.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void endpointForAllUsers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.
                get("/api/test/all")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void endpointForAllUsersWithRoleUser() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.
                get("/api/test/user")).andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void endpointForUsersWithRoleAdmin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.
                get("/api/test/admin")).andExpect(status().isOk());
    }
}
