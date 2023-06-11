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
import pl.plantoplate.REST.controller.utils.ControllerUtils;
import pl.plantoplate.REST.dto.Request.AddToGroupByInviteCodeRequest;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.dto.Response.JwtResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Role;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.service.InviteCodeService;
import pl.plantoplate.REST.service.UserService;

/**
 * REST controller with Endpoints connected with Invite Codes to join group
 */
@RestController
@RequestMapping("api/invite-codes")
@Slf4j
public class InviteCodeController {


    private final InviteCodeService inviteCodeService;
    private final UserService userService;
    private final ControllerUtils controllerUtils;

    @Autowired
    public InviteCodeController(InviteCodeService inviteCodeService, UserService userService, ControllerUtils controllerUtils) {
        this.inviteCodeService = inviteCodeService;
        this.userService = userService;
        this.controllerUtils = controllerUtils;
    }


    /**
     * Users invite code - adds user with provided email to group by invite code. Delete invite code if it is valid. Activates added user account.
     * @param addToGroupByInviteCodeRequest DTO with email, password and invite code
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.JwtResponse} with generated JWT token and role
     */
    @PostMapping()
    @Operation(summary="Join group by invite code",description = "Adds user to group by existing not expired invite code. " +
            "Activates user's account (set isActive = true")
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

        inviteCodeService.verifyInviteCodeAndAddUserToGroup(addToGroupByInviteCodeRequest.getEmail(), addToGroupByInviteCodeRequest.getCode());

        return controllerUtils.generateJwtToken(addToGroupByInviteCodeRequest.getEmail(), addToGroupByInviteCodeRequest.getPassword());
    }


    /**
     * Generates and saves invite code if user has role ADMIN. Expiration time of invite code is 30 minutes. Invite code is generate to group of user with
     * provided role as Request Param ?role= USER or ADMIN.
     * @param role ADMIN or USER
     * @return ResponseEntity parametrized with {@link pl.plantoplate.REST.dto.Response.CodeResponse} with generated invite code.
     */
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Generates code to invite new user to your group as ADMIN or USER",
            description = "Generates invite code with provided as request param role (?role = USER or ?role=ADMIN) and for join group of authenticated user with role ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API sends back invite code",  content = @Content(
                            schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Role value is wrong or group of user not found",  content = @Content(
                            schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity generateInviteCode(@RequestParam("role") @Parameter(schema = @Schema(description = "role",type = "string", allowableValues = {"USER", "ADMIN"})) String role){


        try{
            Role.valueOf("ROLE_" + role);
        }catch (IllegalArgumentException e){
            throw new WrongRequestData("Query keys available - USER and ADMIN");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        int generateCode = controllerUtils.generateCode(100000, 899999);
        long groupId = userService.findByEmail(email).getUserGroup().getId();

        inviteCodeService.saveCode(generateCode, groupId, Role.valueOf("ROLE_" + role));

        return ResponseEntity.ok(new CodeResponse(generateCode));
    }


}
