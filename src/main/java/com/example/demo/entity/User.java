package com.example.demo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private Integer tokenVersion;  // Token 版本号，用于单点登录
    private Date createTime;
    private Date updateTime;

}