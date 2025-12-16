package com.example.demo.mapper;

import com.example.demo.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    Admin findByUsername(String username);
    
    Admin findById(Long id);
    
    // 更新 token 版本号
    int updateTokenVersion(@Param("id") Long id, @Param("tokenVersion") Integer tokenVersion);
}