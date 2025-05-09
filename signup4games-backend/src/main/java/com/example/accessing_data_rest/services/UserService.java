package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Retrieve a user by ID with better exception handling
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Create a new user with input validation
    public User createUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }
        return userRepository.save(user);
    }

    // Update an existing user with better null checks
    public User updateUser(Long id, User updatedUser) {
        if (updatedUser.getName() == null || updatedUser.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Delete a user by ID with improved logging
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
        System.out.println("✅ User with ID " + id + " deleted successfully.");
    }
}