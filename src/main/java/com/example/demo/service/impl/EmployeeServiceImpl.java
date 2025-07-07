package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 员工服务实现类
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeMapper.findAll();
    }

    @Override
    public Employee getEmployeeById(Integer id) {
        return employeeMapper.findById(id);
    }

    @Override
    @Transactional
    public ApiResponse addEmployee(Employee employee) {
        try {
            boolean result = employeeMapper.insert(employee);
            return result ? 
                ApiResponse.success("员工添加成功", employee.getId()) : 
                ApiResponse.error("员工添加失败");
        } catch (Exception e) {
            return ApiResponse.error("添加员工失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse updateEmployee(Employee employee) {
        try {
            boolean result = employeeMapper.update(employee);
            return result ? 
                ApiResponse.success("员工更新成功", employee.getId()) : 
                ApiResponse.error("员工更新失败");
        } catch (Exception e) {
            return ApiResponse.error("更新员工失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse deleteEmployee(Integer id) {
        try {
            boolean result = employeeMapper.delete(id);
            return result ? 
                ApiResponse.success("员工删除成功", id) : 
                ApiResponse.error("员工删除失败");
        } catch (Exception e) {
            return ApiResponse.error("删除员工失败：" + e.getMessage());
        }
    }
}