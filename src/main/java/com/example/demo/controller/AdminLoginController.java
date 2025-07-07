package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.LoginRequest;
import com.example.demo.service.AdminLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    @Autowired
    private AdminLoginService adminLoginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        ApiResponse response = adminLoginService.login(request.getUsername(), request.getPassword());
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
}