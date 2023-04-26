/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.
 */

package pl.plantoplate.REST.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.Group;
import pl.plantoplate.REST.entity.Role;
import pl.plantoplate.REST.entity.User;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.UserRepository;

@Service
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }


    // TODO create custom Exception
    /**
     * Create new group and add user as admin to this group
     * @param email
     */
    public void createGroupAndAddAdmin(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException());
        user.setRole(Role.ROLE_ADMIN);
        user.setActive(true);

        if(user.getUserGroup() == null) {
            Group group = new Group();
            group.addUser(user);
            Group savedGroup = groupRepository.save(group);

            log.info("User with email [" + email + "] created group with id [" + savedGroup.getId() + "]");
        }


    }

    // TODO create custom Exception
    @Transactional(readOnly = true)
    public Group findById(long id){
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException());
    }
}
