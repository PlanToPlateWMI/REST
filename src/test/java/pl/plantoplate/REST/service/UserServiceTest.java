package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.plantoplate.REST.dto.Request.EmailRoleRequest;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.exception.*;
import pl.plantoplate.REST.firebase.PushNotificationService;
import pl.plantoplate.REST.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Service Test")
public class UserServiceTest {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private PushNotificationService pushNotificationService;

    @BeforeEach
    void setUp(){
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder, pushNotificationService);
    }

    @Test
    @DisplayName("Save user")
    void shouldSaveCorrectUser(){

        //given
        User savedUser = new User();

        //when
        userRepository.save(savedUser);

        //then
        verify(userRepository).save(savedUser);
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    @DisplayName("Find user by Email")
    void shouldFindUserByCorrectEmail(){

        String email = "test@gmail.com";

        //when
        userRepository.findByEmail(email);

        //then
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    @DisplayName("Find User by Email")
    void shouldFindUserByEmail() throws EntityNotFound {

        //given
        String email = "test@gamil.com";
        User userWithEmail = new User();
        userWithEmail.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(userWithEmail));

        //when
        var userFromService = userService.findByEmail(email);

        //then
        assertEquals(userFromService, userWithEmail);
    }

    @Test
    @DisplayName("Throw Exception when User with email doesn't exist")
    void shouldThrowExceptionWhenUserNotExists(){

        //given
        String email = "test@gamil.com";
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(null));

        //when
        EntityNotFound e = assertThrows(EntityNotFound.class, () -> userService.findByEmail(email));

        //then
        assertTrue(e.getMessage().contains(email));
    }


    @Test
    @DisplayName("Should update password")
    void shouldUpdatePassword() throws EntityNotFound {
        //given
        String email = "test@gmail.com";
        String newPassword = "new";
        String oldPassword = "old";

        User user = new User();
        user.setPassword(oldPassword);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));


        //when
        userService.resetPassword(email, newPassword);

        //then
        assertEquals(user.getPassword(), newPassword);

    }


    @Test
    void shouldRegisterUserWhenHeNotExists(){

        //given
        String email = "email";
        String password = "password";
        String login = "login";
        String fcmToken = "fcmToken";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(new User()));

        //when
        userService.registerUser(email, password, login, fcmToken);


        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User saved = userArgumentCaptor.getValue();

        assertEquals(password, saved.getPassword());
        assertEquals(login, saved.getUsername());

    }


    @Test
    void shouldRegisterUserWhenHeExists(){
        //given
        String email = "email";
        String password = "password";
        String login = "login";
        String fcmtoken = "token";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        //when
        userService.registerUser(email, password, login, fcmtoken);


        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User saved = userArgumentCaptor.getValue();

        assertEquals(password, saved.getPassword());
        assertEquals(login, saved.getUsername());
    }

    @Test
    void shouldUpdateUsername(){
        //given
        String oldUsername = "old";
        String newUsername = "new";
        String email = "test@gmail.com";

        User user = new User();
        user.setUsername(oldUsername);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        //when
        userService.updateUsername(email, newUsername);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(newUsername, capturedUser.getUsername());
    }

    @Test
    void shouldMatchPassword(){
        //given
        String email = "test@gmail.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        //when
        boolean isMatch = userService.isPasswordMatch(email, password);

        //then
        assertTrue(isMatch);
    }

    @Test
    void shouldUpdatePasswordOfAuthenticatesUser(){
        //given
        String email = "test@gmail.com";
        String password = "newPassword";
        String username = "username";


        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        //when
        userService.updatePassword(email, passwordEncoder.encode(password));

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertTrue(passwordEncoder.matches(password, capturedUser.getPassword()));
    }


    @Test
    void shouldUpdateEmail(){
        //given
        String email = "test@gmail.com";
        String newEmail = "test2@gmail.com";
        String password = "password";
        String username = "username";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(userRepository.existsByEmailAndIsActiveTrue(newEmail)).thenReturn(false);

        //when
        userService.updateEmail(email,newEmail);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(newEmail, capturedUser.getEmail());

    }


    @Test
    void shouldThrowExceptionWhenTryToUpdateEmailAndUserWithThisEmailExists(){
        //given
        String email = "test@gmail.com";
        String newEmail = "test2@gmail.com";
        String password = "password";
        String username = "username";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(userRepository.existsByEmailAndIsActiveTrue(newEmail)).thenReturn(true);

        //when
        assertThrows(EmailAlreadyTaken.class, () -> userService.updateEmail(email,newEmail));
    }


    @Test
    void shouldThrowExceptionWhenRoleIsWrong(){
        String userEmail = "test@gmail.com";
        List<EmailRoleRequest> emailRoleRequestList = new ArrayList<>();
        EmailRoleRequest request = new EmailRoleRequest("wrong role", "email");
        emailRoleRequestList.add(request);

        assertThrows(WrongRequestData.class, () -> userService.updateRoles(userEmail, emailRoleRequestList));
    }

    @Test
    void shouldThrowExceptionWhenUserNotHasGroup(){
        String userEmail = "test@gmail.com";
        List<EmailRoleRequest> emailRoleRequestList = new ArrayList<>();
        EmailRoleRequest request = new EmailRoleRequest("ADMIN", "email");
        emailRoleRequestList.add(request);

        User user = new User();
        user.setEmail(userEmail);
        user.setUserGroup(null);

        when(userRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(user));
        assertThrows(EntityNotFound.class, () -> userService.updateRoles(userEmail, emailRoleRequestList));
    }


    @Test
    void shouldThrowExceptionWhenUserTryToChangeHisRole(){
        String userEmail = "test@gmail.com";
        List<EmailRoleRequest> emailRoleRequestList = new ArrayList<>();
        EmailRoleRequest request = new EmailRoleRequest("ADMIN", userEmail);
        emailRoleRequestList.add(request);

        User user = new User();
        user.setEmail(userEmail);
        user.setUserGroup(new Group());
        user.setEmail(userEmail);

        when(userRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(user));
        assertThrows(UserChangeHisRole.class, () -> userService.updateRoles(userEmail, emailRoleRequestList));
    }

    @Test
    void shouldThrowExceptionWhenUserTryToChangeRoleOfUserNotFromHisGroup(){
        String userEmail = "test@gmail.com";
        String emailOfUserNotFromGroup = "test2@gmail.com";
        List<EmailRoleRequest> emailRoleRequestList = new ArrayList<>();
        EmailRoleRequest request = new EmailRoleRequest("ADMIN", emailOfUserNotFromGroup);
        emailRoleRequestList.add(request);

        User user = new User();
        user.setEmail(userEmail);
        user.setUserGroup(new Group());
        user.setEmail(userEmail);


        when(userRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(user));
        assertThrows(UserNotFromGroup.class, () -> userService.updateRoles(userEmail, emailRoleRequestList));
    }

    @Test
    void shouldUpdateUsersRoles(){
        String userEmail = "test@gmail.com";
        String emailOfUserFromGroup = "test2@gmail.com";
        List<EmailRoleRequest> emailRoleRequestList = new ArrayList<>();
        EmailRoleRequest request = new EmailRoleRequest("ADMIN", emailOfUserFromGroup);
        emailRoleRequestList.add(request);

        Group group = new Group();

        User user = new User();
        user.setEmail(userEmail);
        user.setUserGroup(group);
        user.setEmail(userEmail);
        user.setRole(Role.ROLE_ADMIN);

        User user2 = new User();
        user2.setEmail(userEmail);
        user2.setUserGroup(group);
        user2.setEmail(emailOfUserFromGroup);
        user.setRole(Role.ROLE_USER);

        group.setUsers(List.of(user, user2));

        when(userRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(user));
        when(userRepository.findByEmail(emailOfUserFromGroup)).thenReturn(java.util.Optional.of(user2));

        List<User> returnedUsers = userService.updateRoles(userEmail, emailRoleRequestList);

        assertTrue(returnedUsers.stream().anyMatch(e-> e.getEmail().equals(emailOfUserFromGroup) && e.getRole().equals(Role.ROLE_ADMIN)));
        verify(userRepository).save(user2);
    }

    @Test
    void shouldReturnUserOfGroup(){
        //given
        String email = "test@gmail.com";
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

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        //when
        List<User> usersOfGroup = userService.getUserOfTheSameGroup(email);


        //then
        assertEquals(2, usersOfGroup.size());
        assertTrue(usersOfGroup.contains(user));
        assertTrue(usersOfGroup.contains(user1));
    }
}
