package com.example.demo.dto;

import java.math.BigDecimal;

/**
 * 样品数据传输对象
 */
public class SampleDTO {
    
    /**
     * 样品ID
     */
    private Long id;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户企业名称
     */
    private String companyName;
    
    /**
     * 别称
     */
    private String alias;
    
    /**
     * 型号
     */
    private String model;
    
    /**
     * 色号
     */
    private String colorCode;
    
    /**
     * 图片路径
     */
    private String image;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 单价
     */
    private BigDecimal unitPrice;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
