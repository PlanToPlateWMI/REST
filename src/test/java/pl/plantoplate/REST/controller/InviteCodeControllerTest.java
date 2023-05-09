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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.dto.Request.AddToGroupByInviteCodeRequest;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.dto.Response.JwtResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.repository.UserRepository;
import pl.plantoplate.REST.security.JwtUtils;
import pl.plantoplate.REST.service.InviteCodeService;
import pl.plantoplate.REST.service.UserService;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("InviteController Test")
public class InviteCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtUtils utils;


    private static ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private InviteCodeService inviteCodeService;

    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }


    @Test
    @DisplayName("Return BadRequest if user with email not exists and try to use invite code to join group")
    void shouldReturnBadRequestWhenUserNotExistsAndTryToJoinInviteCode() throws Exception{

        //get
        String email = "test@gamil.com";
        String password = "password";
        AddToGroupByInviteCodeRequest request = new AddToGroupByInviteCodeRequest(123456, email, password);
        when(userService.existsByEmail(email)).thenReturn(false);

        //when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/invite-codes")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(inviteCodeService);

    }

    @Test
    @DisplayName("Return valid JWT token and add user to group by invite code")
    void shouldReturnTokenAndAddUserToGroupByInviteCode() throws Exception{

        //given
        String email = "test@gamil.com";
        String password = "password";
        String username = "username";
        int code = 123456;
        AddToGroupByInviteCodeRequest request = new AddToGroupByInviteCodeRequest(code, email, password);
        when(userService.existsByEmail(email)).thenReturn(true);

        User user = new User(username, encoder.encode(password), email);
        user.setRole(Role.ROLE_USER);
        user.setActive(true);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/invite-codes")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertTrue(utils.isJwtTokenValid(mapper.readValue(mvcResult.getResponse().getContentAsString(), JwtResponse.class).getToken()));
        verify(inviteCodeService).verifyInviteCodeAndAddUserToGroup(email, code);
    }

    @Test
    @DisplayName("Return BadRequest if user try to add invite code with role not ADMIN or USER")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestIfRoleIsInvalid() throws Exception{

        String invalidRole = "test";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/invite-codes?role=" +invalidRole))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Return Forbidden if user without role ADMIN try to add invite code")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenIfNotAdminTryToAddInviteCode() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/api/invite-codes?role=USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Add invite code with correct role to group of user")
    @WithMockUser(roles = "ADMIN")
    void shouldAddInviteCodeOfValidUserAndReturnGenerateInviteCode() throws Exception{

        //get
        String email = "user";
        String password = "password";
        String username = "username";

        String roleOfInviteCode = "USER";

        when(userService.existsByEmail(email)).thenReturn(true);

        User user = new User(username, encoder.encode(password), email);
        user.setRole(Role.ROLE_ADMIN);
        user.setActive(true);

        Group group = new Group();
        long groupId = 1L;
        group.setId(groupId);
        group.addUser(user);
        user.setUserGroup(group);

        when(userService.findByEmail(email)).thenReturn(user);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/invite-codes?role="+roleOfInviteCode))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ArgumentCaptor<Integer> codeArgumentCapture = ArgumentCaptor.forClass(Integer.class);
        verify(inviteCodeService).saveCode(codeArgumentCapture.capture(), anyLong(), any(Role.class));
        assertEquals(codeArgumentCapture.getValue(), mapper.readValue(mvcResult.getResponse().getContentAsString(), CodeResponse.class).getCode());


    }


}
