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
 * 实现EmployeeService接口，提供员工信息管理相关的业务逻辑
 * 负责处理员工的增删改查等操作
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    /**
     * 员工数据访问对象
     * 用于对员工信息进行CRUD操作
     */
    private final EmployeeMapper employeeMapper;

    /**
     * 构造函数注入EmployeeMapper
     * 使用构造器注入方式，便于单元测试和提高代码可靠性
     *
     * @param employeeMapper 员工数据访问对象
     */
    @Autowired
    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    /**
     * 获取所有员工信息
     *
     * @return 员工列表
     */
    @Override
    public List<Employee> getAllEmployees() {
        // 调用数据访问对象查询所有员工
        return employeeMapper.findAll();
    }

    /**
     * 根据ID获取员工信息
     *
     * @param id 员工ID
     * @return 员工对象，如果不存在则返回null
     */
    @Override
    public Employee getEmployeeById(Integer id) {
        // 调用数据访问对象根据ID查询员工
        return employeeMapper.findById(id);
    }

    /**
     * 添加新员工
     * 使用事务确保数据一致性
     *
     * @param employee 要添加的员工对象
     * @return 添加结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse addEmployee(Employee employee) {
        try {
            // 调用数据访问对象插入员工记录
            boolean result = employeeMapper.insert(employee);
            return result ? 
                ApiResponse.success("员工添加成功", employee.getId()) : // 添加成功
                ApiResponse.error("员工添加失败"); // 添加失败
        } catch (Exception e) {
            // 捕获异常，回滚事务
            return ApiResponse.error("添加员工失败：" + e.getMessage());
        }
    }

    /**
     * 更新员工信息
     * 使用事务确保数据一致性
     *
     * @param employee 更新后的员工对象
     * @return 更新结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse updateEmployee(Employee employee) {
        try {
            // 调用数据访问对象更新员工记录
            boolean result = employeeMapper.update(employee);
            return result ? 
                ApiResponse.success("员工更新成功", employee.getId()) : // 更新成功
                ApiResponse.error("员工更新失败"); // 更新失败
        } catch (Exception e) {
            // 捕获异常，回滚事务
            return ApiResponse.error("更新员工失败：" + e.getMessage());
        }
    }

    /**
     * 删除指定员工
     * 使用事务确保数据一致性
     *
     * @param id 要删除的员工ID
     * @return 删除结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse deleteEmployee(Integer id) {
        try {
            // 调用数据访问对象删除员工记录
            boolean result = employeeMapper.delete(id);
            return result ? 
                ApiResponse.success("员工删除成功", id) : // 删除成功
                ApiResponse.error("员工删除失败"); // 删除失败
        } catch (Exception e) {
            // 捕获异常，回滚事务
            return ApiResponse.error("删除员工失败：" + e.getMessage());
        }
    }
}