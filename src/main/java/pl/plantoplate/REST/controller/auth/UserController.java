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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.utils.ControllerUtils;
import pl.plantoplate.REST.dto.Request.EmailPasswordRequest;
import pl.plantoplate.REST.dto.Request.EmailRoleRequest;
import pl.plantoplate.REST.dto.Request.PasswordRequest;
import pl.plantoplate.REST.dto.Request.UsernameRequest;
import pl.plantoplate.REST.dto.Response.JwtResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.dto.Response.UsernameRoleEmailResponse;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.firebase.PushNotificationService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller with Endpoints connected Users settings
 */
@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ControllerUtils controllerUtils;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, ControllerUtils controllerUtils) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.controllerUtils = controllerUtils;
    }


    /**
     * Checks is active user with provided email exists
     * @param email email to check
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.SimpleResponse}. If HTTP STATUS is CONFLICT - exists, OK  - not exists
     */
    @GetMapping("/emails")
    @Operation(summary="Check if active user with provided email exists. Returns information if user with email exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doesn't exist active user with email",  content = @Content(
                   schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "409", description = "Exists active user with email",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> existsUserByEmail(@RequestParam("email") String email){
        if(userService.existsByEmailAndActiveTrue(email)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleResponse("User with email [" + email + "] already exists"));
        }

        return new ResponseEntity<>(new SimpleResponse("Not exists user with email [" + email + "]"), HttpStatus.OK);
    }


    /**
     * Updates username of authenticated user
     * @param usernameRequest DTO with new username
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.UsernameRoleEmailResponse} with updated user info
     */
    @PatchMapping("/username")
    @Operation(summary="Updates username." , description = "Updates username of authenticated user. Returns updated user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username changed successfully",  content = @Content(
                    schema = @Schema(implementation = UsernameRoleEmailResponse.class))),
            @ApiResponse(responseCode = "400", description = "User not found",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<UsernameRoleEmailResponse> changeUsername(@RequestBody UsernameRequest usernameRequest){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User updatedUser = userService.updateUsername(email, usernameRequest.getUsername());
        return new ResponseEntity<>(new UsernameRoleEmailResponse(updatedUser),
                HttpStatus.OK);
    }

    /**
     * Updates password of authenticated user
     * @param passwordRequest DTO with new password
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.UsernameRoleEmailResponse} with updated user info
     */
    @PatchMapping("/password")
    @Operation(summary="Updates password." , description = "Updates password of authenticated user. Returns updated user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully",  content = @Content(
                    schema = @Schema(implementation = UsernameRoleEmailResponse.class))),
            @ApiResponse(responseCode = "400", description = "User not found",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<UsernameRoleEmailResponse> changePassword(@RequestBody PasswordRequest passwordRequest){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String encodedPassword = passwordEncoder.encode(passwordRequest.getPassword());
        User updatedUser = userService.updatePassword(email, encodedPassword);
        return new ResponseEntity<>(new UsernameRoleEmailResponse(updatedUser),
                HttpStatus.OK);
    }


    /**
     * Updates email of authenticated user if active user with provided email doesn't exist. Generates new JWT token.
     * @param emailPasswordRequest DTO with password and new email
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.JwtResponse} with generated JWT toke and role
     */
    @PatchMapping("/email")
    @Operation(summary="Updates email.", description = "Updates email of authenticated user. Returns updated user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully",  content = @Content(
                    schema = @Schema(implementation = UsernameRoleEmailResponse.class))),
            @ApiResponse(responseCode = "400", description = "User not found",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email is already taken",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<JwtResponse> changeEmail(@RequestBody EmailPasswordRequest emailPasswordRequest){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updateEmail(email, emailPasswordRequest.getEmail());
        return controllerUtils.generateJwtToken(emailPasswordRequest.getEmail(), emailPasswordRequest.getPassword());
    }


    /**
     * Checks if provided as Request Param ?password matches password in DATABSE of authenticated user
     * @param password password to check if matches
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.SimpleResponse}. If HTTP STATUS is CONFLICT - doesn't match, OK  - matches
     */
    @GetMapping("password/match")
    @Operation(summary="Check is provided password matches with password of authenticated user", description =
            "Check is provided password matches with password of authenticated user. Return HTTP STATUS CONFLICT if doesn't match and" +
                    "OK if matches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password matches with password from DB",  content = @Content(
                    array = @ArraySchema( schema = @Schema(implementation = SimpleResponse.class)))),
            @ApiResponse(responseCode = "409", description = "Password doesn't match with password from DB",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "User not found",  content = @Content(
                    array = @ArraySchema( schema = @Schema(implementation = SimpleResponse.class))))
    })
    public ResponseEntity<SimpleResponse> isPasswordMatches(@RequestParam("password") String password){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isMatch = userService.isPasswordMatch(email, password);
        if(isMatch)
            return new ResponseEntity<>(new SimpleResponse("Password of user [" + email + "] matches"), HttpStatus.OK);
        return new ResponseEntity<>(new SimpleResponse("Password of user [" + email + "] doesn't match"), HttpStatus.CONFLICT);
    }


    /**
     * Returns information of users of group of authenticated user.
     * @return ResponseEntity parametrized with List of {@link pl.plantoplate.REST.dto.Response.UsernameRoleEmailResponse}
     */
    @GetMapping("infos")
    @Operation(summary="Returns information of users of group of authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's info",  content = @Content(
                    array = @ArraySchema( schema = @Schema(implementation = UsernameRoleEmailResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User not found or user doesn't have group",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<List<UsernameRoleEmailResponse>> listOfUsersInfo(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> usersList = userService.getUserOfTheSameGroup(email);
        return new ResponseEntity<>(usersList.stream().map(UsernameRoleEmailResponse::new).collect(Collectors.toList()), HttpStatus.OK);
    }


    /**
     * Returns information about authenticated user.
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.UsernameRoleEmailResponse}
     */
    @GetMapping()
    @Operation(summary="Returns information of authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Info of authenticated user",  content = @Content(
                  schema = @Schema(implementation = UsernameRoleEmailResponse.class))),
            @ApiResponse(responseCode = "400", description = "User not found or user doesn't have group",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<UsernameRoleEmailResponse> userAuthenticatedInfo(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userService.findByEmail(email);
        return new ResponseEntity<>( new UsernameRoleEmailResponse(authenticatedUser), HttpStatus.OK);
    }


    /**
     * Changes roles of users in group of authenticated user with role ADMIN by email. Valid roles - ADMIN and USEr
     * @param requests List of {@link pl.plantoplate.REST.dto.Request.UsernameRequest} with email and new roles of users.
     * @return ResponseEntity parametrized with List of {@link pl.plantoplate.REST.dto.Response.UsernameRoleEmailResponse} with updated information of users in group
     */
    @PatchMapping("roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Update roles of users in group by email.",
            description = "User with role ADMIN can updates roles of members of his group by their email addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's info",  content = @Content(
                    array = @ArraySchema( schema = @Schema(implementation = UsernameRoleEmailResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User doesn't have group or role in body isn't valid",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "409", description = "At least one user with email not from user's group or user try to change his role",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<List<UsernameRoleEmailResponse>> updatedRoles(@RequestBody List<EmailRoleRequest> requests){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> groupUsers = userService.updateRoles(email, requests);
        return new ResponseEntity<>(groupUsers.stream().map(UsernameRoleEmailResponse::new).collect(Collectors.toList()), HttpStatus.OK);
    }
}
