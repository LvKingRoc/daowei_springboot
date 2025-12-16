package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单数据传输对象
 */
@Data
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
}
