package com.example.simplyfoundapis.services;

import com.example.simplyfoundapis.models.User;
import com.example.simplyfoundapis.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // POST - Create new user
    public User saveNewUser(User newUser) {
        return userRepository.save(newUser);
    }

    // GET - Retrieve all users
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // GET - Retrieve user by ID
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    // DELETE - Delete user by ID
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    // PUT - Update user info
    public User updateUser(User newUser) {
        User oldUser = userRepository.findById(newUser.getId()).get();
        oldUser.setEmail(newUser.getEmail());
        oldUser.setPassword(newUser.getPassword());
        oldUser.setName(newUser.getName());
        return userRepository.save(oldUser);
    }

    // Login functionality
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return "Login successful!";
        } else {
            return "Invalid email or password!";
        }
    }
}
