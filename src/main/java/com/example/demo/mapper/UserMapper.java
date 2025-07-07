package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    List<User> findAll();
    int insertUser(User user); // 新增用户
    int updateUser(User user); // 修改用户信息
    int deleteUser(Long id); // 删除用户
}