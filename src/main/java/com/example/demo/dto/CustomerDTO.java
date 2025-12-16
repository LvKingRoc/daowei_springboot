package com.example.demo.dto;

import lombok.Data;
import java.util.List;

/**
 * 客户数据传输对象
 * 用于API请求和响应的数据传输
 */
@Data
public class CustomerDTO {
    
    /**
     * 客户ID
     */
    private Long id;
    
    /**
     * 企业名称
     */
    private String companyName;
    
    /**
     * 地址列表
     */
    private List<AddressDTO> addresses;
    
    /**
     * 联系人列表
     */
    private List<ContactDTO> contacts;
}
