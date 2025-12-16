package com.example.demo.dto;

import lombok.Data;

/**
 * 联系人数据传输对象
 */
@Data
public class ContactDTO {
    
    /**
     * 联系人ID
     */
    private Long id;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 联系人姓名
     */
    private String contactName;
    
    /**
     * 联系电话
     */
    private String phone;
}
