package com.example.demo.entity;

import java.util.Date;
import java.util.List;

public class Customer {
    private Long id;
    private String companyName;
    private Date createTime;
    private Date updateTime;
    private List<CustomerAddress> addresses;
    private List<CustomerContact> contacts;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public List<CustomerAddress> getAddresses() { return addresses; }
    public void setAddresses(List<CustomerAddress> addresses) { this.addresses = addresses; }
    public List<CustomerContact> getContacts() { return contacts; }
    public void setContacts(List<CustomerContact> contacts) { this.contacts = contacts; }
}