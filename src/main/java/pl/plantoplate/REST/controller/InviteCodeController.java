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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.utils.ControllerJwtUtils;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.dto.Request.addToGroupByInviteCodeRequest;
import pl.plantoplate.REST.dto.Response.JwtResponse;
import pl.plantoplate.REST.entity.Role;
import pl.plantoplate.REST.service.InviteCodeService;
import pl.plantoplate.REST.service.UserService;


@RestController
@RequestMapping("invite_code")
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


    // TODO fix problem with Forbidden
    /**
     * If inviteCode exists - add user to group of this code. Send back JWT token
     * @param addToGroupByInviteCodeRequest
     * @return JWT token
     */
    @PostMapping()
    public ResponseEntity<JwtResponse> addUserToGroupByInviteCode(@RequestBody addToGroupByInviteCodeRequest addToGroupByInviteCodeRequest){
        inviteCodeService.verifyInviteCodeAndAddUserToGroup(addToGroupByInviteCodeRequest.getEmail(), addToGroupByInviteCodeRequest.getCode());
        String wrongPassword = userService.findByEmail(addToGroupByInviteCodeRequest.getEmail()).getPassword();
        return controllerUtils.generateJwtToken(addToGroupByInviteCodeRequest.getEmail(), wrongPassword);
    }


    /**
     * Admin can generate invite code to group to the ADMIn or USER.
     * @param role - ADMIN or USER
     * @return generated code
     */
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CodeResponse> generateInviteCode(@RequestParam("role") String role){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        int generateCode = controllerUtils.generateCode(100000, 899999);
        long groupId = userService.findByEmail(email).getUserGroup().getId();

        inviteCodeService.saveCode(generateCode, groupId, Role.valueOf("ROLE_" + role));

        return ResponseEntity.ok(new CodeResponse(generateCode));
    }


}
