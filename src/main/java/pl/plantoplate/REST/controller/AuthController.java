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

package pl.plantoplate.REST.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.utils.ControllerUtils;
import pl.plantoplate.REST.dto.Request.LoginRequest;
import pl.plantoplate.REST.dto.Request.SignupRequest;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.dto.Response.JwtResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.Role;
import pl.plantoplate.REST.entity.User;
import pl.plantoplate.REST.exception.UserNotFound;
import pl.plantoplate.REST.mail.MailParams;
import pl.plantoplate.REST.mail.MailSenderService;
import pl.plantoplate.REST.service.GroupService;
import pl.plantoplate.REST.service.UserService;



@RestController
@RequestMapping("api/auth/")
@Slf4j
public class AuthController {


    private final ControllerUtils controllerUtils;
    private final UserService userService;
    private final GroupService groupService;
    private final PasswordEncoder encoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public AuthController(ControllerUtils controllerUtils, UserService userService, GroupService groupService, PasswordEncoder encoder, MailSenderService mailSenderService) {
        this.controllerUtils = controllerUtils;
        this.userService = userService;
        this.groupService = groupService;
        this.encoder = encoder;
        this.mailSenderService = mailSenderService;
    }

    /**
     * Create User's account. Before it check if user with this email already
     * exists in DB. If it is so when we returns HttpStatus.CONFLICT.
     * If user with email are not used by other user when send code to user's email and
     * return this code to mobile app
     * @param userSignupInfo
     * @return verification code send to email address
     */
    @PostMapping("signup")
    @Operation(summary="Create an account",description = "User can create an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered and API sends back code that it sends yo user's email",content = @Content(
                                                            schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email is already taken", content = @Content(
                                                            schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity registerUser(@RequestBody SignupRequest userSignupInfo){

        if(userService.existsByEmail(userSignupInfo.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleResponse(String.format("User with email already exists", userSignupInfo.getEmail())));
        }

        // create User
        User user  = new User(userSignupInfo.getUsername(),
                encoder.encode(userSignupInfo.getPassword()), userSignupInfo.getEmail());
        // set User's role to USER, isActivated - false
        user.setRole(Role.ROLE_USER);
        user.setActive(false);

        // save new User in DB
        userService.save(user);

        //generate code and send it to user's email address
        int code = ControllerUtils.generateCode(1000, 8999);
        mailSenderService.send(new MailParams(code, userSignupInfo.getEmail()));

        log.info("User with email [ " + userSignupInfo.getEmail() +"] started registration");

        return ResponseEntity.ok(new CodeResponse(code));
    }


    /**
     * Generate JWT token by user email and password
     * @param loginRequest
     * @return JWT token and role
     */
    @PostMapping("signin")
    @Operation(summary="Sigin",description = "User can login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully login and API sends back JWT Token and role", content = @Content(
                                                                                            schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist", content = @Content(
                                                                                            schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity authenticateUser(@RequestBody LoginRequest loginRequest){

        if (!userService.existsByEmail(loginRequest.getEmail())) {
            return new ResponseEntity<>(
                    new SimpleResponse(String.format("User with email %s doesn't exist", loginRequest.getEmail())), HttpStatus.BAD_REQUEST);
       }

        log.info("User with email [ " + loginRequest.getEmail() +"] try to signin");

        return controllerUtils.generateJwtToken(loginRequest.getEmail(), loginRequest.getPassword());
    }



    /**
     * Create new group for user provided by email. Set this user Role - Role.ADMIN
     * @return jwt token and role
     */
    @PostMapping("/group")
    @Operation(summary="Create new group",description = "User can create his own group and he will have ROLE_ADMIN ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API sends back JWT Token and role",  content = @Content(
                                                                    schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                                                                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity createGroup(@RequestBody LoginRequest loginRequest){

        try {
            groupService.createGroupAndAddAdmin(loginRequest.getEmail());
        }catch (UserNotFound e){
            return new ResponseEntity(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return controllerUtils.generateJwtToken(loginRequest.getEmail(), loginRequest.getPassword());
    }


}
