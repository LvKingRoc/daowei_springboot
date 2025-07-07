package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        ApiResponse response = userLoginService.login(request.getUsername(), request.getPassword());
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse> findAll() {
        List<User> users = userLoginService.findAll();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addUser(@RequestBody User user) {
        ApiResponse response = userLoginService.insertUser(user);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody User user) {
        ApiResponse response = userLoginService.updateUser(user);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        ApiResponse response = userLoginService.deleteUser(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
}