package com.elephant.server.controllers;

import com.elephant.server.message.Message;
import com.elephant.server.models.User;
import com.elephant.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    UserRepository userRepository;
    ArrayList<Message> messages = new ArrayList<>();

    @GetMapping
    public List<Message> getMessages() {
        return messages;
    }

    @PostMapping
    public Message createMessage(@RequestBody Message message) {
        messages.add(message);
        return message;
    }

    @GetMapping("/test")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
