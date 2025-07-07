package com.example.demo.mapper;

import com.example.demo.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    Order getById(Long id);
    List<Order> listBySampleId(@Param("sampleId") Long sampleId);
    int insert(Order order);
    int update(Order order);
    int delete(Long id);
    List<Order> listAll();
    
    /**
     * 根据客户名称查询订单数量
     */
    int countByCompanyName(@Param("companyName") String companyName);
}