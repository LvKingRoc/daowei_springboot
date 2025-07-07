package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDate;
@Data
public class Employee {
    private Integer id;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String idCard;
    private LocalDate hireDate;
}