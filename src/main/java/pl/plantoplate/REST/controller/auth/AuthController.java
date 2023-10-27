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

package pl.plantoplate.REST.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.controller.utils.ControllerUtils;
import pl.plantoplate.REST.dto.Request.EmailPasswordRequest;
import pl.plantoplate.REST.dto.Request.SignupRequest;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.dto.Response.JwtResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.mail.EmailType;
import pl.plantoplate.REST.mail.MailParams;
import pl.plantoplate.REST.mail.MailSenderService;
import pl.plantoplate.REST.service.GroupService;
import pl.plantoplate.REST.service.UserService;


/**
 * REST controller with Endpoints connected with Authentication and Authorization
 */
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
     * Create account by email, username and password.
     * If email isn't already taken - sends generated code to provided email
     * @param userSignupInfo DTO with username, email and password
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.CodeResponse} with generated code sent to provided email address
     */
    @PostMapping("signup")
    @Operation(summary="Creates an account",description = "Creates new account by email, username and password. Sends code to confirm email address " +
            "to provided email. Set role to USER and isActive to false. To activate account user must create his group /api/auth/group or join group by invite code /api/invite-codes." +
            "Returns code what was send to provided email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered and API sends back code that it sends yo user's email",content = @Content(
                                                            schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email is already taken", content = @Content(
                                                            schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity registerUser(@RequestBody SignupRequest userSignupInfo){

        if(userService.existsByEmailAndActiveTrue(userSignupInfo.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleResponse(String.format("User with email already exists", userSignupInfo.getEmail())));
        }

        userService.registerUser(userSignupInfo.getEmail(),
                                 encoder.encode(userSignupInfo.getPassword()),
                                 userSignupInfo.getUsername(),
                                 userSignupInfo.getFcmToken());


        //generate code and send it to user's email address
        int code = ControllerUtils.generateCode(1000, 8999);
        mailSenderService.send(new MailParams(code, userSignupInfo.getEmail()), EmailType.registration);

        log.info("User with email [ " + userSignupInfo.getEmail() +"] started registration");

        return ResponseEntity.ok(new CodeResponse(code));
    }


    /**
     * Generate JWT token by provided email and password
     * @param emailPasswordRequest DTO with email and password
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.JwtResponse} with JWT token and role
     */
    @PostMapping("signin")
    @Operation(summary="Sing in to existing account.",description = "Authenticates user by provided email and password. If credentials are " +
            " correct - generate JWT token. Returns generated JWT token and user's role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully login and API sends back JWT Token and role", content = @Content(
                                                                                            schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist", content = @Content(
                                                                                            schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity authenticateUser(@RequestBody EmailPasswordRequest emailPasswordRequest){

        if (!userService.existsByEmail(emailPasswordRequest.getEmail())) {
            return new ResponseEntity<>(
                    new SimpleResponse(String.format("User with email %s doesn't exist", emailPasswordRequest.getEmail())), HttpStatus.BAD_REQUEST);
       }

        log.info("User with email [ " + emailPasswordRequest.getEmail() +"] try to signin");

        return controllerUtils.generateJwtToken(emailPasswordRequest.getEmail(), emailPasswordRequest.getPassword());
    }



    /**
     * Create new group for user provided by email. Set role to {@link pl.plantoplate.REST.entity.auth.Role#ROLE_ADMIN}
     * @param emailPasswordRequest DTO with email and password
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.JwtResponse} with JWT token and role
     */
    @PostMapping("/group")
    @Operation(summary="Create new group",description = "Created new group for user by provided email. Set user's role to ADMIN and activate user's account. (set isActivate to true)" +
            "Generates JWT token. Returns generated JWT token and user's role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API sends back JWT Token and role",  content = @Content(
                                                                    schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                                                                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity createGroup(@RequestBody EmailPasswordRequest emailPasswordRequest){

        groupService.createGroupAndAddAdmin(emailPasswordRequest.getEmail());

        return controllerUtils.generateJwtToken(emailPasswordRequest.getEmail(), emailPasswordRequest.getPassword());
    }

    /**
     * Updates user's password
     * @param emailPasswordRequest DTO with email and password
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.SimpleResponse}
     */
    @PostMapping("/password/reset")
    @Operation(summary="Reset password",description = "Updated user's password by provided email. Returns information that password was updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API update password",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity resetPassword(@RequestBody EmailPasswordRequest emailPasswordRequest){

        String encodedPassword = encoder.encode(emailPasswordRequest.getPassword());

        log.info("User with email [" + emailPasswordRequest.getPassword() + "] try to reset password");

        userService.resetPassword(emailPasswordRequest.getEmail(), encodedPassword);

        return ResponseEntity.ok(new SimpleResponse("Password was successfully updated"));
    }



}
