package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.exception.UserNotFound;
import pl.plantoplate.REST.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Service Test")
public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
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
    void shouldFindUserByEmail() throws UserNotFound {

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
        UserNotFound e = assertThrows(UserNotFound.class, () -> userService.findByEmail(email));

        //then
        assertTrue(e.getMessage().contains(email));
    }


    @Test
    @DisplayName("Should update password")
    void shouldUpdatePassword() throws UserNotFound {
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
}
