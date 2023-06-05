package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Service Test")
public class UserServiceTest {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
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
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(new User()));

        //when
        userService.registerUser(email, password, login);


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
        when(userRepository.existsByEmail(email)).thenReturn(false);

        //when
        userService.registerUser(email, password, login);


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
        String email = "test@gmail.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        boolean isMatch = userService.isPasswordMatch(email, password);

        assertTrue(isMatch);
    }
}
