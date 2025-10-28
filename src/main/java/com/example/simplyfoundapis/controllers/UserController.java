package com.example.simplyfoundapis.controllers;

import com.example.simplyfoundapis.models.User;
import com.example.simplyfoundapis.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")

public class UserController {
    @Autowired
    private UserService userService;

    // POST - Create new user
    @PostMapping("/post")
    public User post(@RequestBody User user) {
        return userService.saveNewUser(user);
    }

    // GET - Retrieve all users
    @GetMapping("/all")
    public List<User> findAll() {
        return userService.findAll();
    }

    // GET - Retrieve user by ID
    @GetMapping("/{id}")
    public Optional<User> findById(@PathVariable int id) {
        return userService.findById(id);
    }

    // PUT - Update user by ID
    @PutMapping("/{id}")
    public User update(@PathVariable int id, @RequestBody User user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    // DELETE - Delete user by ID
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable int id) {
        userService.deleteById(id);
    }
    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String response = userService.login(email, password);
        return ResponseEntity.ok(response);
    }
}
