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

    // Retrieve a user by ID with improved exception handling
    public Optional<User> getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        return userRepository.findById(id);
    }

    // Create a new user with input validation and consistent logging
    public User createUser(User user) {
        if (user == null || user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }

        User savedUser = userRepository.save(user);
        System.out.println("✅ User created with ID: " + savedUser.getUid() + " and name: " + savedUser.getName());
        return savedUser;
    }

    // Update an existing user with better null checks and logging
    public User updateUser(Long id, User updatedUser) {
        if (id == null || updatedUser == null || updatedUser.getName() == null || updatedUser.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID and name cannot be null or empty.");
        }

        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    User savedUser = userRepository.save(user);
                    System.out.println("✅ User with ID " + savedUser.getUid() + " updated successfully.");
                    return savedUser;
                })
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found."));
    }

    // Delete a user by ID with improved logging
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User with ID " + id + " not found.");
        }
        userRepository.deleteById(id);
        System.out.println("✅ User with ID " + id + " deleted successfully.");
    }
}