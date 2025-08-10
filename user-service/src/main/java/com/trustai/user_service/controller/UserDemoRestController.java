package com.trustai.user_service.controller;


import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserDemoRestController {
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String sayHello() {
        return "user-service-running";
    }

    @GetMapping("/create")
    public List<User> createUsers() {
        for (int i=1; i<= 10; i++) {
            String username = "user_" + i;
            User user = new User(username, "RANK_1", BigDecimal.ZERO);
            user.setEmail(username + "@example.com");
            userRepository.save(user);
        }
        return userRepository.findAll();
    }
}
