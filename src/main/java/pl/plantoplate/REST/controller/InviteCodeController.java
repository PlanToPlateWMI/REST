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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.utils.ControllerJwtUtils;
import pl.plantoplate.REST.dto.Request.AddToGroupByInviteCodeRequest;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.dto.Response.JwtResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.Role;
import pl.plantoplate.REST.exception.GroupNotFound;
import pl.plantoplate.REST.exception.UserNotFound;
import pl.plantoplate.REST.exception.WrongInviteCode;
import pl.plantoplate.REST.service.InviteCodeService;
import pl.plantoplate.REST.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/invite-codes")
@Slf4j
public class InviteCodeController {


    private final InviteCodeService inviteCodeService;
    private final UserService userService;
    private final ControllerJwtUtils controllerUtils;

    @Autowired
    public InviteCodeController(InviteCodeService inviteCodeService, UserService userService, ControllerJwtUtils controllerUtils) {
        this.inviteCodeService = inviteCodeService;
        this.userService = userService;
        this.controllerUtils = controllerUtils;
    }


    /**
     * If inviteCode exists - add user to group of this code. Send back JWT token
     * If invite code is wrong than - return  BadRequest status
     * @param addToGroupByInviteCodeRequest
     * @return JWT token
     */
    @PostMapping()
    @Operation(summary="Join group",description = "User can join group by invite code ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API sends back JWT Token and role",  content = @Content(
                                schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invite code is wrong or user with this email doesn't exist",  content = @Content(
                                schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity addUserToGroupByInviteCode(@RequestBody AddToGroupByInviteCodeRequest addToGroupByInviteCodeRequest){

        if(!userService.existsByEmail(addToGroupByInviteCodeRequest.getEmail())){
            return new ResponseEntity(
                    new SimpleResponse(String.format("User with email %s doesn't exist", addToGroupByInviteCodeRequest.getEmail())), HttpStatus.BAD_REQUEST);
        }

        try {
            inviteCodeService.verifyInviteCodeAndAddUserToGroup(addToGroupByInviteCodeRequest.getEmail(), addToGroupByInviteCodeRequest.getCode());
        }catch (WrongInviteCode | UserNotFound e){
            return ResponseEntity.badRequest().body(new SimpleResponse("Invite code is wrong or expired"));
        }

        return controllerUtils.generateJwtToken(addToGroupByInviteCodeRequest.getEmail(), addToGroupByInviteCodeRequest.getPassword());
    }


    /**
     * Admin can generate invite code to group to the ADMIN or USER.
     * @param role - ADMIN or USER
     * @return generated code
     */
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Generate code to invite new user as ADMIN or USER",description = "User with role ADMIN can invite user to his group by invite code. Invite code has expiration time ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API sends back invite code",  content = @Content(
                            schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Role value is wrong",  content = @Content(
                            schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity generateInviteCode(@RequestParam("role") @Parameter(schema = @Schema(description = "role",type = "string", allowableValues = {"USER", "ROLE"})) String role){

        List<String> availableRoles = Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList());

        if(!availableRoles.contains("ROLE_" + role)){
            return ResponseEntity.badRequest().body(new SimpleResponse("Role value is wrong. Available roles : ADMIN, USER"));
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        int generateCode = controllerUtils.generateCode(100000, 899999);
        long groupId = 0;

        try {
            groupId = userService.findByEmail(email).getUserGroup().getId();
        }catch (UserNotFound e){
            return ResponseEntity.badRequest().body(new SimpleResponse(e.getMessage()));
        }

        try {
            inviteCodeService.saveCode(generateCode, groupId, Role.valueOf("ROLE_" + role));
        }catch (GroupNotFound e){
            return ResponseEntity.badRequest().body(new SimpleResponse(e.getMessage()));
        }

        return ResponseEntity.ok(new CodeResponse(generateCode));
    }


}
