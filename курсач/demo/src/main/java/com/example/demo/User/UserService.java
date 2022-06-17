package com.example.demo.User;

public interface UserService {
    void save(User user);
    User findByUsername(String username);
    void remove(User user);
}