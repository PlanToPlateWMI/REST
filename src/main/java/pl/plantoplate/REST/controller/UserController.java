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
import pl.plantoplate.REST.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

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


    @GetMapping("/emails")
    @Operation(summary="Is user with email exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Not exists active user with email",  content = @Content(
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



    //TODO - discuss logic of changing username
    @PatchMapping("/username")
    @Operation(summary="Change username. Return updated user info")
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

    @PatchMapping("/password")
    @Operation(summary="Change password. Return updated user info")
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


    @PatchMapping("/email")
    @Operation(summary="Change email. Return new JWT token and user role.")
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

    @GetMapping("password/match")
    @Operation(summary="Return 200 if password of user matches, 409 - if not. ")
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



    @GetMapping("infos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Info of all user of group : username, role, email ")
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


    @GetMapping()
    @Operation(summary="Info of authenticated user")
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


    @PatchMapping("roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Update roles by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's info",  content = @Content(
                    array = @ArraySchema( schema = @Schema(implementation = UsernameRoleEmailResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User doesn't have group or role in body isn't valid",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "409", description = "At least one user with email",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "409", description = "At least one user with email",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<List<UsernameRoleEmailResponse>> updatedRoles(@RequestBody List<EmailRoleRequest> requests){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> groupUsers = userService.updateRoles(email, requests);
        return new ResponseEntity<>(groupUsers.stream().map(UsernameRoleEmailResponse::new).collect(Collectors.toList()), HttpStatus.OK);
    }
}
