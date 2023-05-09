package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.InviteCode;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.exception.GroupNotFound;
import pl.plantoplate.REST.exception.UserNotFound;
import pl.plantoplate.REST.exception.WrongInviteCode;
import pl.plantoplate.REST.repository.InviteCodeRepository;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Invite code Test")
public class InviteCodeServiceTest {

    private InviteCodeService inviteCodeService;
    private UserService userService;
    private GroupService groupService;
    private InviteCodeRepository inviteCodeRepository;

    @BeforeEach
    void setUp(){
        userService = mock(UserService.class);
        groupService = mock(GroupService.class);
        inviteCodeRepository = mock(InviteCodeRepository.class);
        inviteCodeService = new InviteCodeService(inviteCodeRepository, userService, groupService);
    }


    @Test
    void shouldSaveCodeToCorrectGroupWithCorrectRole() throws GroupNotFound {

        //given
        long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);

        int code = 1111;
        Role role = Role.ROLE_ADMIN;

        when(groupService.findById(groupId)).thenReturn(group);

        //when
        inviteCodeService.saveCode(code, groupId, role);
        LocalTime time = LocalTime.now();

        //then
        ArgumentCaptor<InviteCode> inviteCodeArgumentCaptor = ArgumentCaptor.forClass(InviteCode.class);
        verify(inviteCodeRepository).save(inviteCodeArgumentCaptor.capture());
        InviteCode savedInviteCode = inviteCodeArgumentCaptor.getValue();

        assertEquals(savedInviteCode.getCode(), code);
        assertEquals(savedInviteCode.getGroup(), group);
        assertEquals(savedInviteCode.getRole(), role);

    }


    @Test
    @DisplayName("throws exception if code doesnt exist")
    void shouldThrowExceptionIfInviteNotExists(){

        //given
        int code = 1111;
        String email = "test@gmail.com";
        when(inviteCodeRepository.existsByCode(code)).thenReturn(false);

        //when
        WrongInviteCode e = assertThrows(WrongInviteCode.class, () -> inviteCodeService.
                verifyInviteCodeAndAddUserToGroup(email, code));

        assertTrue(e.getMessage().contains(String.valueOf(code)));
    }

    @Test
    @DisplayName("throws exception if code is expired")
    void shouldThrowExceptionIfInviteIsExpired(){
        //given
        int code = 1111;
        String email = "test@gmail.com";
        when(inviteCodeRepository.existsByCode(code)).thenReturn(true);

        InviteCode inviteCode = new InviteCode();
        inviteCode.setExpiredTime(LocalTime.now().minusMinutes(1));

        when(inviteCodeRepository.getByCode(code)).thenReturn(inviteCode);

        //when
        WrongInviteCode e = assertThrows(WrongInviteCode.class, () ->
                inviteCodeService.verifyInviteCodeAndAddUserToGroup(email, code));

        assertTrue(e.getMessage().contains(String.valueOf(code)));

    }

    @Test
    @DisplayName("add user to group by invite code")
    void shouldAddUserToGroupAndSetIsActiveAndSetRoleIfInviteCodeIsCorrect() throws UserNotFound, WrongInviteCode {
        //given
        int code = 1111;
        String email = "test@gmail.com";
        Role role = Role.ROLE_ADMIN;
        when(inviteCodeRepository.existsByCode(code)).thenReturn(true);

        User user = new User();
        user.setEmail(email);
        when(userService.findByEmail(email)).thenReturn(user);

        Group group = new Group();

        InviteCode inviteCode = new InviteCode(code, group, role, LocalTime.now().plusMinutes(10));
        when(inviteCodeRepository.getByCode(code)).thenReturn(inviteCode);

        //when
        inviteCodeService.verifyInviteCodeAndAddUserToGroup(email, code);

        //then
        assertTrue(user.isActive());
        assertEquals(user.getRole(), role);
        assertTrue(group.getUsers().contains(user));

        verify(inviteCodeRepository).delete(inviteCode);


    }
}
