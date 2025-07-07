package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    private Long id;
    private String orderNumber;
    private Long sampleId;
    private String model;
    private String colorCode;
    private String companyName;
    private String image;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private Date createDate;
    private Date deliveryDate;
    private String status;
    private Date createTime;
    private Date updateTime;
}