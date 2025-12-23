package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单数据传输对象
 */
public class OrderDTO {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNumber;
    
    /**
     * 样品ID
     */
    private Long sampleId;
    
    /**
     * 型号
     */
    private String model;
    
    /**
     * 色号
     */
    private String colorCode;
    
    /**
     * 客户企业名称
     */
    private String companyName;
    
    /**
     * 图片路径
     */
    private String image;
    
    /**
     * 总数量
     */
    private Integer totalQuantity;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单创建日期
     */
    private Date createDate;
    
    /**
     * 交货日期
     */
    private Date deliveryDate;
    
    /**
     * 订单状态
     */
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public Long getSampleId() { return sampleId; }
    public void setSampleId(Long sampleId) { this.sampleId = sampleId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Date getCreateDate() { return createDate; }
    public void setCreateDate(Date createDate) { this.createDate = createDate; }
    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
