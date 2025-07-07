package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.User;

import java.util.List;

public interface UserLoginService {
    ApiResponse login(String username, String password);
    List<User> findAll();
    ApiResponse insertUser(User user);
    ApiResponse updateUser(User user);
    ApiResponse deleteUser(Long id);
}