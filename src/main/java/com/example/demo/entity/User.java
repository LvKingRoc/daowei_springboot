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
    private Date createTime;
    private Date updateTime;

}