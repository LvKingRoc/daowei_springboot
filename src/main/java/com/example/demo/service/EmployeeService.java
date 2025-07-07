package com.example.demo.service;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Employee;

import java.util.List;

public interface EmployeeService {
    
    /**
     * 获取所有员工
     */
    List<Employee> getAllEmployees();

    /**
     * 根据ID获取员工
     */
    Employee getEmployeeById(Integer id);

    /**
     * 添加员工
     */
    ApiResponse addEmployee(Employee employee);

    /**
     * 更新员工
     */
    ApiResponse updateEmployee(Employee employee);

    /**
     * 删除员工
     */
    ApiResponse deleteEmployee(Integer id);
}