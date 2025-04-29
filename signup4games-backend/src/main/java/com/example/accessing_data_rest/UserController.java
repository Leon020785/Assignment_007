package com.example.accessing_data_rest;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.UserRepository;
import com.example.accessing_data_rest.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roborally/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Hent alle brugere
    @GetMapping(value = "", produces = "application/json")
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    // Søg efter brugere via navn
    @GetMapping(value = "/searchusers", produces = "application/json")
    public List<User> searchUsers(@RequestParam("name") String name) {
        return userRepository.findByName(name);
    }

    // Opret en ny bruger
    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public User postUser(@RequestBody User user) {
        return userRepository.save(user);
    }






}
