package com.elephant.server;

import com.elephant.server.models.User;
import com.elephant.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Autowired
    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void run(ApplicationArguments args) {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setRole("USER");
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return;
        }
        if (user.getId() != null) {
            if (userRepository.findById(user.getId()).isPresent()) {
                return;
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.saveAndFlush(user);
        return;
    }
}