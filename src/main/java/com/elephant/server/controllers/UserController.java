package com.elephant.server.controllers;

import com.elephant.server.models.User;
import com.elephant.server.repositories.UserRepository;
import com.elephant.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private FileService fileService;

    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return null;
        }
        if (user.getId() != null) {
            if (userRepository.findById(user.getId()).isPresent()) {
                return null;
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.saveAndFlush(user);
        return user;
    }

    @GetMapping("/test")
    public boolean testCredentials() {
        return true;
    }

}
