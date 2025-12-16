package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 样品数据传输对象
 */
@Data
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
}
