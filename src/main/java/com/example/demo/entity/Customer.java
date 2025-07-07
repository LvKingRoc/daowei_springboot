package com.example.demo.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Customer {
    private Long id;
    private String companyName;
    private Date createTime;
    private Date updateTime;
    private List<CustomerAddress> addresses;
    private List<CustomerContact> contacts;
}