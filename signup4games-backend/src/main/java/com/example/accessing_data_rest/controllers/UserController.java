package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roborally/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "", produces = "application/json")
    public List<User> getAllUsers() {
        System.out.println("📋 Fetching all users...");
        List<User> users = (List<User>) userRepository.findAll();
        System.out.println("✅ Found " + users.size() + " users.");
        return users;
    }

    @GetMapping(value = "/searchusers", produces = "application/json")
    public List<User> searchUsers(@RequestParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }
        System.out.println("🔍 Searching for users with name: " + name);
        List<User> users = userRepository.findByName(name);
        if (users.isEmpty()) {
            System.out.println("⚠️ No users found with the name: " + name);
        } else {
            System.out.println("✅ Found " + users.size() + " user(s) with the name: " + name);
        }
        return users;
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public User postUser(@RequestBody User user) {
        if (user == null || user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }
        User savedUser = userRepository.save(user);
        System.out.println("✅ User created with ID: " + savedUser.getUid() + " and name: " + savedUser.getName());
        return savedUser;
    }


}
