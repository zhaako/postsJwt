package com.jwt.security.services;

import com.jwt.security.user.User;

import java.util.List;

public interface UserService {
    List<User> getAllUser();
    User getUser(int id);
    User getCurrentUser();
}
