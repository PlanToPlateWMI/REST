package pl.plantoplate.REST.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.Group;
import pl.plantoplate.REST.entity.User;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.UserRepository;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public void addUserToGroup(long groupId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException());
        Group group = groupRepository.getById(groupId);
        group.getUsers().add(user);
    }


}
