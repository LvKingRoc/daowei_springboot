package com.example.demo.entity;

import lombok.Data;
import java.util.Date;

@Data
public class CustomerContact {
    private Long id;
    private Long customerId;
    private String contactName;
    private String phone;
    private Date createTime;
    private Date updateTime;
}