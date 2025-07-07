package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ApiResponse login(String username, String password) {
        User user = userMapper.findByUsername(username);

        if (user == null) {
            return ApiResponse.error("用户名不存在");
        }

        if (!user.getPassword().equals(password)) {
            return ApiResponse.error("密码错误");
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("token", "user_token_" + user.getId());
        userData.put("user", user);

        return ApiResponse.success("登录成功", userData);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public ApiResponse insertUser(User user) {
        try {
            int result = userMapper.insertUser(user);
            if (result > 0) {
                return ApiResponse.success("用户添加成功", user.getId());
            } else {
                return ApiResponse.error("用户添加失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("用户添加失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponse updateUser(User user) {
        try {
            int result = userMapper.updateUser(user);
            if (result > 0) {
                return ApiResponse.success("用户更新成功", user.getId());
            } else {
                return ApiResponse.error("用户更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("用户更新失败：" + e.getMessage());
        }
    }

    @Override
    public ApiResponse deleteUser(Long id) {
        try {
            int result = userMapper.deleteUser(id);
            if (result > 0) {
                return ApiResponse.success("用户删除成功", id);
            } else {
                return ApiResponse.error("用户删除失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("用户删除失败：" + e.getMessage());
        }
    }
}