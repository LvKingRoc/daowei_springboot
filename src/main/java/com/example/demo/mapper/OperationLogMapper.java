package com.example.demo.mapper;

import com.example.demo.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface OperationLogMapper {

    /**
     * 插入日志记录
     */
    int insert(OperationLog log);

    /**
     * 查询所有日志（分页由PageHelper处理）
     */
    List<OperationLog> selectAll();

    /**
     * 根据条件查询日志
     */
    List<OperationLog> selectByCondition(
            @Param("operatorName") String operatorName,
            @Param("module") String module,
            @Param("action") String action,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );

    /**
     * 根据ID查询日志详情
     */
    OperationLog selectById(@Param("id") Long id);

    /**
     * 删除指定日期之前的日志
     */
    int deleteByDate(@Param("date") String date);

    /**
     * 统计日志总数
     */
    int count();

    /**
     * 根据操作人姓名和模块查询日志（用于system用户单条日志）
     */
    OperationLog selectByOperatorNameAndModule(@Param("operatorName") String operatorName, @Param("module") String module);

    /**
     * 更新日志记录
     */
    int update(OperationLog log);

    /**
     * 根据操作类型删除日志
     */
    int deleteByAction(@Param("action") String action);
}
