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

package pl.plantoplate.REST.controller.utils;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.plantoplate.REST.controller.dto.response.JwtResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.security.JwtUtils;
import pl.plantoplate.REST.security.UserDetailsImpl;
import pl.plantoplate.REST.service.UserService;

import java.util.Random;

@Component
@AllArgsConstructor
public class ControllerUtils {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;


    public ResponseEntity<JwtResponse> generateJwtToken(String email, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().get();

        return ResponseEntity.ok(new JwtResponse(jwt,
                role));
    }

    public static int generateCode(int start, int bound){
        Random r = new Random();
        int number = start + r.nextInt(bound);
        return number;

    }

    public Group authorizeUserByEmail(){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findGroupOfUser(email);
    }
}
