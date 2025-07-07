package com.example.demo.entity;

import lombok.Data;
import java.util.Date;

@Data
public class CustomerAddress {
    private Long id;
    private Long customerId;
    private String address;
    private Date createTime;
    private Date updateTime;
}