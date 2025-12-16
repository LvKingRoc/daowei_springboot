package com.example.demo.dto;

import lombok.Data;

/**
 * 地址数据传输对象
 */
@Data
public class AddressDTO {
    
    /**
     * 地址ID
     */
    private Long id;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 地址详情
     */
    private String address;
}
