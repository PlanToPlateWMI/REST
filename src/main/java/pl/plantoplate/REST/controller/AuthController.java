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
import pl.plantoplate.REST.dto.JwtResponse;
import pl.plantoplate.REST.dto.LoginRequest;
import pl.plantoplate.REST.dto.SignupDto;
import pl.plantoplate.REST.dto.SimpleResponse;
import pl.plantoplate.REST.entity.Role;
import pl.plantoplate.REST.entity.User;
import pl.plantoplate.REST.repository.UserRepository;
import pl.plantoplate.REST.security.JwtUtils;
import pl.plantoplate.REST.security.UserDetailsImpl;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("api/auth/")
public class AuthController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Create User's account. Before it we should check if user with this login and email already
     * exists in DB. If it is so when we returns HttpStatus.CONFLICT.
     * If user with login and email are not used by other user when return code what we send to user's email
     * to submit his email address
     * @param userSignupInfo
     * @return
     */
    @PostMapping("signup")
    public ResponseEntity<SimpleResponse> registerUser(@RequestBody SignupDto userSignupInfo){

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

        //TODO generate 4-number code and send it by email


        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        System.out.println(secretString);

        return ResponseEntity.ok(new SimpleResponse("User registered successfully"));
    }


    /**
     * Generate JWT token by user login and password
     * @param loginRequest
     * @return
     */
    @PostMapping("test")
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
}
