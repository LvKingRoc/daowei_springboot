package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Employee;

import java.util.List;

/**
 * 员工服务接口
 * 提供员工信息管理相关的业务逻辑
 * 包括员工的增删改查操作
 */
public interface EmployeeService {
    
    /**
     * 获取所有员工信息
     * 
     * @return 员工列表
     */
    List<Employee> getAllEmployees();

    /**
     * 根据ID获取员工信息
     * 
     * @param id 员工ID
     * @return 员工对象，如果不存在则返回null
     */
    Employee getEmployeeById(Integer id);

    /**
     * 添加新员工
     * 
     * @param employee 要添加的员工对象
     * @return 添加结果的API响应
     */
    ApiResponse addEmployee(Employee employee);

    /**
     * 更新员工信息
     * 
     * @param employee 更新后的员工对象
     * @return 更新结果的API响应
     */
    ApiResponse updateEmployee(Employee employee);

    /**
     * 删除指定员工
     * 
     * @param id 要删除的员工ID
     * @return 删除结果的API响应
     */
    ApiResponse deleteEmployee(Integer id);
}