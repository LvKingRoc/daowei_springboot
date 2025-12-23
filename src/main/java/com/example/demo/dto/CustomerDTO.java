package com.example.demo.dto;

import java.util.List;

/**
 * 客户数据传输对象
 * 用于API请求和响应的数据传输
 */
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public List<AddressDTO> getAddresses() { return addresses; }
    public void setAddresses(List<AddressDTO> addresses) { this.addresses = addresses; }
    public List<ContactDTO> getContacts() { return contacts; }
    public void setContacts(List<ContactDTO> contacts) { this.contacts = contacts; }
}
