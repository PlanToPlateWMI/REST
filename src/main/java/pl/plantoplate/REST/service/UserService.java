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

package pl.plantoplate.REST.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.exception.UserNotFound;
import pl.plantoplate.REST.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User save(User user){
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Transactional(readOnly = true)
    public User findByEmail(String email) throws UserNotFound {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFound("User with email [ "  + email + " ] not found"));
    }

    public void resetPassword(String email, String newPassword) throws UserNotFound {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFound("User with email [ "  + email + " ] not found"));
        user.setPassword(newPassword);
        userRepository.save(user);

    }
}
