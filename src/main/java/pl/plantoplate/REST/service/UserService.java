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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.controller.dto.request.EmailRoleRequest;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.exception.*;
import pl.plantoplate.REST.firebase.PushNotificationService;
import pl.plantoplate.REST.repository.UserRepository;

import java.util.List;

/**
 * Service Layer of User JPA Repository
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PushNotificationService pushNotificationService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PushNotificationService pushNotificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.pushNotificationService = pushNotificationService;
    }


    /**
     * Save {@link pl.plantoplate.REST.entity.auth.User}
     * @param user user object to save
     * @return
     */
    public User save(User user){
        return userRepository.save(user);
    }

    /**
     * Check if user {@link pl.plantoplate.REST.entity.auth.User} with email exists
     * @param email email to check
     * @return true user exists, false- doesn't exist
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Returs {@link pl.plantoplate.REST.entity.auth.User} by email.
     * Throws {@link pl.plantoplate.REST.exception.EntityNotFound} if user not found
     * @param email user's email
     * @return User with provided email
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ "  + email + " ] not found"));
    }

    /**
     * Update password of {@link pl.plantoplate.REST.entity.auth.User} by user's email
     * Throws {@link pl.plantoplate.REST.exception.EntityNotFound} if user not found
     * @param email email of user
     * @param newPassword new password
     */
    public void resetPassword(String email, String newPassword)  {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ "  + email + " ] not found"));
        user.setPassword(newPassword);
        userRepository.save(user);

    }

    /**
     * Return user's group by user's email
     * @param email user's email
     * @return group {@link pl.plantoplate.REST.entity.auth.Group} of user
     */
    @Transactional(readOnly = true)
    public Group findGroupOfUser(String email) {
        return this.findByEmail(email).getUserGroup();
    }


    /**
     * Check if {@link pl.plantoplate.REST.entity.auth.User} with parametr isActive true and email exists
     * @param email user's email
     * @return true user exists, false - not exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmailAndActiveTrue(String email) {
        return userRepository.existsByEmailAndIsActiveTrue(email);
    }

    /**
     * Save user {@link pl.plantoplate.REST.entity.auth.User} with provided email, username, password
     * with default role {@link pl.plantoplate.REST.entity.auth.Role#ROLE_USER} and active - false
     * @param email user's email
     * @param password user's password
     * @param username user's username
     */
    public void registerUser(String email,
                             String password,
                             String username,
                             String fcmToken) {

        if(userRepository.existsByEmail(email)){
            User user = userRepository.findByEmail(email).get();
            user.setPassword(password);
            user.setUsername(username);
            user.setRole(Role.ROLE_USER);
            user.setActive(false);
            user.setFcmToken(fcmToken);

            userRepository.save(user);
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);
        user.setRole(Role.ROLE_USER);
        user.setActive(false);
        user.setFcmToken(fcmToken);

        userRepository.save(user);

    }

    /**
     * Update username of user {@link pl.plantoplate.REST.entity.auth.User} by email
     * Throws {@link pl.plantoplate.REST.exception.EntityNotFound} if user not found
     * @param email user's email
     * @param username new username
     * @return updated user {@link pl.plantoplate.REST.entity.auth.User} info
     */
    public User updateUsername(String email, String username) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ "  + email + " ] not found"));
        user.setUsername(username);
        return userRepository.save(user);
    }

    /**
     * Returns list of users {@link pl.plantoplate.REST.entity.auth.User} of the same group as user with provided email
     * Throws {@link pl.plantoplate.REST.exception.EntityNotFound} if user not found
     * @param email user's email
     * @return list of users {@link pl.plantoplate.REST.entity.auth.User} of the same group
     */
    public List<User> getUserOfTheSameGroup(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ "  + email + " ] not found"));
        Group group = user.getUserGroup();
        if(group == null)
            throw new EntityNotFound("Group of user with email [ "  + email + " ] not found");
        return group.getUsers();
    }

    /**
     * Check is password of user {@link pl.plantoplate.REST.entity.auth.User} matches with password from DATABASE
     * Throws {@link pl.plantoplate.REST.exception.EntityNotFound} if user not found
     * @param email user's email
     * @param password user's password to check if matches
     * @return true password matches, false - doesn't match
     */
    public boolean isPasswordMatch(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ "  + email + " ] not found"));
        return passwordEncoder.matches(password, user.getPassword());
    }


    /**
     * Updates password of user{@link pl.plantoplate.REST.entity.auth.User} by email
     * Throws {@link pl.plantoplate.REST.exception.EntityNotFound} if user not found
     * @param email new email of user
     * @param password  password
     * @return update user {@link pl.plantoplate.REST.entity.auth.User} info
     */
    public User updatePassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ "  + email + " ] not found"));
        user.setPassword(password);
        return userRepository.save(user);
    }

    /**
     * Updates email of user{@link pl.plantoplate.REST.entity.auth.User} by email
     * Throws {@link pl.plantoplate.REST.exception.EmailAlreadyTaken} if user with newEmail already exists
     * @param email old users' email
     * @param newEmail new user's email
     */
    public void updateEmail(String email, String newEmail) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ "  + email + " ] not found"));
        if(userRepository.existsByEmailAndIsActiveTrue(newEmail))
            throw new EmailAlreadyTaken("Email [" + newEmail + "] is already taken.");
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    /**
     * Update users roles by email and new role
     * Throws {@link pl.plantoplate.REST.exception.WrongRequestData} if role is not valid
     * Throws {@link pl.plantoplate.REST.exception.EntityNotFound} if user with email not found
     * Throws {@link pl.plantoplate.REST.exception.UserChangeHisRole} if user try to change his role
     * Throws {@link pl.plantoplate.REST.exception.UserNotFromGroup} if user with provided email not from group of user with email
     * @param requests list of emails and new roles of users
     * @param email email of user who want to update roles
     * @return list of updated users
     */
    public List<User> updateRoles(String email, List<EmailRoleRequest> requests) {

        for(EmailRoleRequest request : requests){
            try{
                Role.valueOf("ROLE_" + request.getRole());
            }catch (IllegalArgumentException e){
                throw new WrongRequestData("Available roles - USER and ADMIN");
            }
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFound("User with email [ "  + email + " ] not found"));
        Group group = user.getUserGroup();
        if(group == null)
            throw new EntityNotFound("Group of user with email [ "  + email + " ] not found");

        List<User> groupUsers = group.getUsers();

        if(requests.stream().anyMatch(r -> r.getEmail().equals(email)))
            throw new UserChangeHisRole("User [" + email + "] try to change his role");

        for(EmailRoleRequest request: requests){
            if(groupUsers.stream().noneMatch(u -> u.getEmail().equals(request.getEmail())))
                throw new UserNotFromGroup("User with email [" + request.getEmail() + "] not from group of user with email [" + email + "]");
        }

        for(EmailRoleRequest request: requests) {
            User userFromGroup = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new EntityNotFound("User with email [ "  + request.getEmail() + " ] not found"));
            userFromGroup.setRole(Role.valueOf("ROLE_"  + request.getRole()));
            userRepository.save(userFromGroup);
            pushNotificationService.send(userFromGroup.getFcmToken(), "ROLE", "Your role was changed to " + request.getRole());
        }

        return group.getUsers();
    }
}
