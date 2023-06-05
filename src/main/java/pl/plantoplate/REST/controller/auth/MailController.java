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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.controller.utils.ControllerUtils;
import pl.plantoplate.REST.mail.EmailType;
import pl.plantoplate.REST.dto.Response.CodeResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.mail.MailParams;
import pl.plantoplate.REST.mail.MailSenderService;
import pl.plantoplate.REST.service.UserService;

@RestController
@RequestMapping("api/mail/")
@Slf4j
public class MailController {

    private final UserService userService;
    private final MailSenderService mailSenderService;

    @Autowired
    public MailController(UserService userService, MailSenderService mailSenderService) {
        this.userService = userService;
        this.mailSenderService = mailSenderService;
    }

    /**
     * Generate and send code to the email
     * @param email
     * @return generated code
     */
    @GetMapping("/code")
    @Operation(summary="Send code to email. There are 2 types of email - ?type=registration email to confirm registration and ?type=reset " +
            "email to confirm reset password. Default value is registration. ",description = "If user doesn't receive previously generated code he will able to ask for resending a new code ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API send back code that it sends to user's email", content = @Content(
                    schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist or type of email is invalid", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity generateCodeToEmail(@RequestParam("email") String email,
                                              @RequestParam(value = "type", defaultValue = "registration")
                                              @Parameter(schema = @Schema(description = "type of email to send",type = "string", allowableValues = {"registration", "reset"})) String emailType){


        EmailType emailEnum= null;
        try{
            emailEnum = EmailType.valueOf(emailType);
        }catch (IllegalArgumentException e){
            throw new WrongRequestData("Query keys available - USER and ADMIN");
        }

        if (!userService.existsByEmail(email)) {
            return new ResponseEntity(
                    new SimpleResponse(String.format("User with email %s doesn't exist", email)), HttpStatus.BAD_REQUEST);
        }

        int code = ControllerUtils.generateCode(1000, 8999);
        mailSenderService.send(new MailParams(code, email),emailEnum );
        log.info("For user with email ["+email+"] was generated code ["+code+"] to confirm email");
        return ResponseEntity.ok(new CodeResponse(code));
    }
}
