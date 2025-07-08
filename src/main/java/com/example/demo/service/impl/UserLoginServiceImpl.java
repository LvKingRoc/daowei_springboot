package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户登录与管理服务实现类
 * 实现UserLoginService接口，提供用户登录验证、用户信息管理相关的业务逻辑
 * 负责处理用户的身份验证、查询、添加、更新和删除等操作
 */
@Service
public class UserLoginServiceImpl implements UserLoginService {

    /**
     * 用户数据访问对象
     * 用于对用户数据进行CRUD操作和身份验证
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录验证实现
     * 根据用户名查询用户，验证密码是否匹配
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果的API响应，成功则包含用户信息，失败则包含错误信息
     */
    @Override
    public ApiResponse login(String username, String password) {
        // 根据用户名查询用户
        User user = userMapper.findByUsername(username);

        // 检查用户是否存在
        if (user == null) {
            return ApiResponse.error("用户名不存在");
        }

        // 验证密码（未加密，直接比对）
        // 注意：实际生产环境应使用加密算法比对密码
        if (!user.getPassword().equals(password)) {
            return ApiResponse.error("密码错误");
        }

        // 登录成功，返回用户信息
        return ApiResponse.success("登录成功", user);
    }

    /**
     * 查询所有用户信息
     *
     * @return 用户列表
     */
    @Override
    public List<User> findAll() {
        // 查询所有用户
        return userMapper.findAll();
    }

    /**
     * 添加新用户
     *
     * @param user 要添加的用户对象
     * @return 添加结果的API响应
     */
    @Override
    public ApiResponse insertUser(User user) {
        try {
            // 插入用户记录
            int result = userMapper.insertUser(user);
            if (result > 0) {
                // 添加成功
                return ApiResponse.success("用户添加成功", user.getId());
            } else {
                // 添加失败
                return ApiResponse.error("用户添加失败");
            }
        } catch (Exception e) {
            // 处理异常
            return ApiResponse.error("用户添加失败：" + e.getMessage());
        }
    }

    /**
     * 更新现有用户信息
     *
     * @param user 更新后的用户对象
     * @return 更新结果的API响应
     */
    @Override
    public ApiResponse updateUser(User user) {
        try {
            // 更新用户记录
            int result = userMapper.updateUser(user);
            if (result > 0) {
                // 更新成功
                return ApiResponse.success("用户更新成功", user.getId());
            } else {
                // 更新失败，可能用户不存在
                return ApiResponse.error("用户更新失败");
            }
        } catch (Exception e) {
            // 处理异常
            return ApiResponse.error("用户更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除指定用户
     *
     * @param id 要删除的用户ID
     * @return 删除结果的API响应
     */
    @Override
    public ApiResponse deleteUser(Long id) {
        try {
            // 删除用户记录
            int result = userMapper.deleteUser(id);
            if (result > 0) {
                // 删除成功
                return ApiResponse.success("用户删除成功", id);
            } else {
                // 删除失败，可能用户不存在
                return ApiResponse.error("用户删除失败");
            }
        } catch (Exception e) {
            // 处理异常
            return ApiResponse.error("用户删除失败：" + e.getMessage());
        }
    }
}