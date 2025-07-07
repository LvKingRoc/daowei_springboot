package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工控制器
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 获取所有员工
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    /**
     * 根据ID获取员工
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmployeeById(@PathVariable Integer id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(ApiResponse.success(employee));
        } else {
            return ResponseEntity.ok(ApiResponse.error("员工不存在", 404));
        }
    }

    /**
     * 添加员工
     */
    @PostMapping
    public ResponseEntity<ApiResponse> addEmployee(@RequestBody Employee employee) {
        ApiResponse response = employeeService.addEmployee(employee);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 更新员工
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Integer id, @RequestBody Employee employee) {
        employee.setId(id);
        ApiResponse response = employeeService.updateEmployee(employee);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Integer id) {
        ApiResponse response = employeeService.deleteEmployee(id);
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
}