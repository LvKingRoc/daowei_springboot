package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long id);  // 根据ID查询
    User findByUsername(String username);
    List<User> findAll();
    int insertUser(User user); // 新增用户
    int updateUser(User user); // 修改用户信息
    int deleteUser(Long id); // 删除用户
    
    // 更新 token 版本号
    int updateTokenVersion(@Param("id") Long id, @Param("tokenVersion") Integer tokenVersion);
}