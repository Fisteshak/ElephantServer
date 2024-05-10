package com.elephant.server.controllers;

import com.elephant.server.CloudUser;
import com.elephant.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class AuthController {

    @Autowired
    UserRepository userRepository;


    @GetMapping("/user")
    List<CloudUser> getUsers(@AuthenticationPrincipal CloudUser cloudUser) {
        return (userRepository.findAll());
    }

    @GetMapping("/user/{id}")
    CloudUser getUserById(@PathVariable Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @PostMapping("/user")
    CloudUser createUser(@RequestBody CloudUser cloudUser) {
        userRepository.saveAndFlush(cloudUser);
        return cloudUser;
    }


}
