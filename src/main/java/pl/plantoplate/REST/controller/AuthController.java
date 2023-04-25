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

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.dto.*;
import pl.plantoplate.REST.entity.Group;
import pl.plantoplate.REST.entity.InviteCode;
import pl.plantoplate.REST.entity.Role;
import pl.plantoplate.REST.entity.User;
import pl.plantoplate.REST.mail.MailParams;
import pl.plantoplate.REST.mail.MailSenderService;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.InviteCodeRepository;
import pl.plantoplate.REST.repository.UserRepository;
import pl.plantoplate.REST.security.JwtUtils;
import pl.plantoplate.REST.security.UserDetailsImpl;
import pl.plantoplate.REST.service.GroupService;

import javax.crypto.SecretKey;
import java.util.Random;

@RestController
@RequestMapping("api/auth/")
public class AuthController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MailSenderService mailSenderService;

    /**
     * Create User's account. Before it we should check if user with this login and email already
     * exists in DB. If it is so when we returns HttpStatus.CONFLICT.
     * If user with login and email are not used by other user when return code what we send to user's email
     * to submit his email address
     * @param userSignupInfo
     * @return
     */
    @PostMapping("signup")
    public ResponseEntity registerUser(@RequestBody SignupDto userSignupInfo){

        if(userRepository.existsByEmail(userSignupInfo.getEmail()) && userRepository.existsByEmail(userSignupInfo.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleResponse(String.format("User with login %s or %s email already exists", userSignupInfo.getLogin(), userSignupInfo.getEmail())));
        }

        // create User
        User user  = new User(userSignupInfo.getLogin(),
                encoder.encode(userSignupInfo.getPassword()), userSignupInfo.getEmail());
        // set User's role to USER
        user.setRole(Role.ROLE_USER);
        user.setUsername(userSignupInfo.getLogin());

        // save new User in DB
        userRepository.save(user.getEmail(), user.getLogin(), user.getPassword(), user.getRole().name());

        //
        Integer code = generateCode();
        mailSenderService.send(new MailParams(code, userSignupInfo.getEmail()));


        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        System.out.println(secretString);

        return ResponseEntity.ok(new ActivationCodeDto(code));
    }

    private static Integer generateCode() {
        Random r = new Random();
        int fourDigit = 1000 + r.nextInt(8999);
        return fourDigit;
    }


    /**
     * Generate JWT token by user login and password
     * @param loginRequest
     * @return
     */
    @PostMapping("signin")
    public ResponseEntity authenticateUser(@RequestBody LoginRequest loginRequest){

//        if (!userRepository.existsByLogin(loginRequest.getLogin())) {
//            return new ResponseEntity<>(
//                    new SimpleResponse(String.format("User with login %s doesn't exists", loginRequest.getLogin())), HttpStatus.UNAUTHORIZED);
//        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().get();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getLogin(),
                role));
    }

    /**
     * Generate and send code to the email
     * @param email
     * @return
     */
    @GetMapping("/code")
    public ResponseEntity<ActivationCodeDto> generateCodeToEmail(@RequestParam("email") String email){
        int code = generateCode();
        mailSenderService.send(new MailParams(code, email));
        return ResponseEntity.ok(new ActivationCodeDto(code));
    }


    //TODO - add user to group and send JWT token
//    /**
//     * If user wants to join group when app sends his email to identify user and group code
//     * If such group code exists then we add user to this group and send back jwt token
//     * @param data
//     * @return
//     */
//    @GetMapping("/group/code")
//    public ResponseEntity checkGroupCodeAndAddToGroup(AddToExistingGroupDto data){
//        boolean isCodeExists = inviteCodeRepository.existsByCode(data.getGroupCode());
//        if(isCodeExists){
//            InviteCode inviteCode = inviteCodeRepository.getByCode(data.getGroupCode());
//            long groupId = inviteCode.getGroup().getId();
//            groupService.addUserToGroup(groupId, data.getEmail());
//
//
//        }
//
//
//    }
}
