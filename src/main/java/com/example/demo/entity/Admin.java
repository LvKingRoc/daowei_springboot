package com.example.demo.entity;

import lombok.Data;

import java.util.Date;
@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String name;
    private Integer tokenVersion;  // Token 版本号，用于单点登录
    private Date createTime;
    private Date updateTime;

}