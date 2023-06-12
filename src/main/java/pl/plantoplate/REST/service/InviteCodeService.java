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
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.InviteCode;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.exception.WrongInviteCode;
import pl.plantoplate.REST.repository.InviteCodeRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Service Layer of InviteCode JPA Repository
 */
@Service
@Slf4j
public class InviteCodeService {

    private final InviteCodeRepository inviteCodeRepository;
    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public InviteCodeService(InviteCodeRepository inviteCodeRepository, UserService userService, GroupService groupService) {
        this.inviteCodeRepository = inviteCodeRepository;
        this.userService = userService;
        this.groupService = groupService;
    }

    /**
     * Verify if code exists and is code expired. If code is expired or doesn't exist throws {@link pl.plantoplate.REST.exception.WrongInviteCode}.
     * Add user to group of invite code with role from code and delete code
     * @param userEmail email of user who try to user invite code
     * @param inviteCode invite code
     */
    public void verifyInviteCodeAndAddUserToGroup(String userEmail,int inviteCode) {
            if(inviteCodeRepository.existsByCode(inviteCode)){
                InviteCode code = inviteCodeRepository.getByCode(inviteCode);

                if(!code.getExpiredTime().isBefore(LocalDateTime.now())) {
                    Group group = code.getGroup();
                    User user = userService.findByEmail(userEmail);
                    user.setActive(true);
                    user.setRole(code.getRole());

                    group.addUser(user);

                    inviteCodeRepository.delete(code);

                    log.info("User with email ["+userEmail+"] use invite Code ["+inviteCode+"] and join group with id ["+group.getId()+"]");
                    return;

                }else{
                    log.info("User with email ["+userEmail+"] try to use invite Code ["+inviteCode+"] but it is expired");
                    throw new WrongInviteCode(Integer.toString(inviteCode));
                }
            }

            log.info("User with email ["+userEmail+"] try to use invite Code ["+inviteCode+"] but it isn't valid");

            throw new WrongInviteCode(Integer.toString(inviteCode));

    }

    /**
     * Save invite code to group with role
     * @param code generated invite code to save
     * @param groupId group id of invite code
     * @param role role of invite code
     */
    public void saveCode(int code, long groupId, Role role) {

        Group group = groupService.findById(groupId);

        LocalDateTime time = LocalDateTime.now().plusMinutes(30);
        InviteCode inviteCode = new InviteCode(code, group, role, time);
        inviteCodeRepository.save(inviteCode);

        log.info("Invite code ["+code+"] was saved to group with id ["+group.getId()+"] and role ["+role.name()+"]");
    }
}
