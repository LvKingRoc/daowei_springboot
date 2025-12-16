package com.example.demo.mapper;

import com.example.demo.entity.Sample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SampleMapper {
    Sample selectById(@Param("id") Long id);

    void insert(Sample sample);

    int update(Sample sample);

    void delete(@Param("id") Long id);

    List<Sample> findByNullCustomerId();

    int updateCustomerId(@Param("id") Long id, @Param("customerId") Long customerId);

    List<Sample> selectAll();
    
    /**
     * 根据客户ID查询样板数量
     */
    int countByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 批量更新样品的客户ID（用于删除客户时将关联样品转移到默认客户）
     */
    int updateCustomerIdByOldCustomerId(@Param("oldCustomerId") Long oldCustomerId, @Param("newCustomerId") Long newCustomerId);
}