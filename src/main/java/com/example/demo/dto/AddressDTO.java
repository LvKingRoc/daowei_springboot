package com.example.demo.dto;

/**
 * 地址数据传输对象
 */
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
