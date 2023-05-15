package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Group Service Test")
public class GroupServiceTest {

    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private GroupService groupService;
    private CategoryService categoryService;
    private ProductService productService;
    private ShopProductService shopProductGroup;

    @BeforeEach
    void setUp(){
        groupRepository = mock(GroupRepository.class);
        userRepository = mock(UserRepository.class);
        categoryService = mock(CategoryService.class);
        productService = mock(ProductService.class);
        shopProductGroup = mock(ShopProductService.class);
        groupService = new GroupService(groupRepository, userRepository,categoryService, productService, shopProductGroup);
    }


    @Test
    @DisplayName("Throws exception - group doesn't exist")
    void shouldThrowExceptionIfGroupNotExist(){

        long groupId = 1L;
        assertThrows(EntityNotFound.class, () -> groupService.findById(groupId));
        verify(groupRepository).findById(groupId);
    }

    @Test
    @DisplayName("Return correct group by id")
    void shouldReturnCorrectGroupById() throws EntityNotFound {
        //given
        long groupId = 1L;
        Group group = new Group();
        when(groupRepository.findById(groupId)).thenReturn(java.util.Optional.of(group));

        //when
        groupService.findById(groupId);

        //then
        verify(groupRepository).findById(groupId);
        verifyNoMoreInteractions(groupRepository);
    }


    @Test
    void shouldCreateNewGroupAndAddUserAsAdminIfUserHasNoGroup() throws EntityNotFound {
        //given
        String email = "test@gmail.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        //when
        groupService.createGroupAndAddAdmin(email);

        //then
        assertTrue(user.isActive());
        assertEquals(user.getRole(), Role.ROLE_ADMIN);

        ArgumentCaptor<Group> groupArgumentCaptor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository).save(groupArgumentCaptor.capture());
        Group userGroup = groupArgumentCaptor.getValue();

        assertTrue(userGroup.getUsers().contains(user));
    }


}
