package com.example.demo.service;

import com.example.demo.common.ApiResponse;

public interface AdminLoginService {
    ApiResponse login(String username, String password);
}