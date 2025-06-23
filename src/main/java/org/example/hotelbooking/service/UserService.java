package org.example.hotelbooking.service;

import org.example.hotelbooking.model.UserRole;
import org.example.hotelbooking.model.User;


public interface UserService {
    void registerUser(String email, String password, UserRole role, String firstName, String lastName);
    User findByEmail(String email);
}
