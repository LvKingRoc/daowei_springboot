package com.example.demo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Sample {
    private Long id;
    private Long customerId;
    private String companyName;
    private String alias;
    private String model;
    private String colorCode;
    private String image;
    private Integer stock;
    private BigDecimal unitPrice;
    private Date createTime;
    private Date updateTime;
}