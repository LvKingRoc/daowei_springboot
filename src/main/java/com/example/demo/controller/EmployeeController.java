package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工管理控制器
 * 处理员工相关的HTTP请求
 * 提供员工的增删改查等功能的REST API接口
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    /**
     * 员工服务接口
     * 处理员工相关的业务逻辑
     * 使用构造器注入的方式注入依赖
     */
    private final EmployeeService employeeService;

    /**
     * 构造函数注入EmployeeService
     * 
     * @param employeeService 员工服务实现类
     */
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 获取所有员工信息
     * 
     * @return 包含所有员工列表的HTTP响应
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmployees() {
        // 调用服务获取所有员工列表
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    /**
     * 根据ID获取单个员工信息
     * 
     * @param id 员工ID
     * @return 包含员工信息的HTTP响应，如果员工不存在则返回错误信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmployeeById(@PathVariable Integer id) {
        // 根据ID查询员工
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            // 员工存在，返回员工信息
            return ResponseEntity.ok(ApiResponse.success(employee));
        } else {
            // 员工不存在，返回错误信息
            return ResponseEntity.ok(ApiResponse.error("员工不存在", 404));
        }
    }

    /**
     * 添加新员工
     * 
     * @param employee 员工信息对象
     * @return 添加结果的HTTP响应，包含成功信息或错误信息
     */
    @PostMapping
    public ResponseEntity<ApiResponse> addEmployee(@RequestBody Employee employee) {
        // 调用服务添加员工
        ApiResponse response = employeeService.addEmployee(employee);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : // 添加成功返回200 OK
            ResponseEntity.badRequest().body(response); // 添加失败返回400 Bad Request
    }

    /**
     * 更新员工信息
     * 
     * @param id 员工ID
     * @param employee 更新后的员工信息对象
     * @return 更新结果的HTTP响应，包含成功信息或错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Integer id, @RequestBody Employee employee) {
        // 设置要更新的员工ID
        employee.setId(id);
        // 调用服务更新员工信息
        ApiResponse response = employeeService.updateEmployee(employee);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : // 更新成功返回200 OK
            ResponseEntity.badRequest().body(response); // 更新失败返回400 Bad Request
    }

    /**
     * 删除员工
     * 
     * @param id 要删除的员工ID
     * @return 删除结果的HTTP响应，包含成功信息或错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Integer id) {
        // 调用服务删除员工
        ApiResponse response = employeeService.deleteEmployee(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : // 删除成功返回200 OK
            ResponseEntity.badRequest().body(response); // 删除失败返回400 Bad Request
    }
}