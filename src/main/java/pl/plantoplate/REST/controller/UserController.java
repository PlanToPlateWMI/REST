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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.service.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
