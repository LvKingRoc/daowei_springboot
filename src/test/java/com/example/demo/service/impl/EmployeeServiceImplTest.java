package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EmployeeServiceImpl 白盒测试
 * 覆盖员工 CRUD 的成功/失败/异常全部分支
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeServiceImpl 白盒测试")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    // ==================== getAllEmployees 测试 ====================

    @Test
    @DisplayName("查询所有员工 - 有数据")
    void getAllEmployees_hasData() {
        Employee e1 = createEmployee(1L, "张三");
        Employee e2 = createEmployee(2L, "李四");
        when(employeeMapper.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("查询所有员工 - 空列表")
    void getAllEmployees_empty() {
        when(employeeMapper.findAll()).thenReturn(Collections.emptyList());

        List<Employee> result = employeeService.getAllEmployees();

        assertTrue(result.isEmpty());
    }

    // ==================== getEmployeeById 测试 ====================

    @Test
    @DisplayName("根据ID查询 - 存在")
    void getEmployeeById_found() {
        when(employeeMapper.findById(1L)).thenReturn(createEmployee(1L, "张三"));

        Employee result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals("张三", result.getName());
    }

    @Test
    @DisplayName("根据ID查询 - 不存在")
    void getEmployeeById_notFound() {
        when(employeeMapper.findById(999L)).thenReturn(null);

        Employee result = employeeService.getEmployeeById(999L);

        assertNull(result);
    }

    // ==================== addEmployee 测试 ====================

    @Test
    @DisplayName("添加员工 - 成功")
    void addEmployee_success() {
        Employee employee = createEmployee(null, "新员工");
        when(employeeMapper.insert(any(Employee.class))).thenReturn(true);

        ApiResponse response = employeeService.addEmployee(employee);

        assertTrue(response.isSuccess());
        assertEquals("员工添加成功", response.getMessage());
    }

    @Test
    @DisplayName("添加员工 - 失败（返回false）")
    void addEmployee_fail() {
        Employee employee = createEmployee(null, "新员工");
        when(employeeMapper.insert(any(Employee.class))).thenReturn(false);

        ApiResponse response = employeeService.addEmployee(employee);

        assertFalse(response.isSuccess());
        assertEquals("员工添加失败", response.getMessage());
    }

    @Test
    @DisplayName("添加员工 - 异常")
    void addEmployee_exception() {
        Employee employee = createEmployee(null, "新员工");
        when(employeeMapper.insert(any(Employee.class))).thenThrow(new RuntimeException("数据库异常"));

        ApiResponse response = employeeService.addEmployee(employee);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("数据库异常"));
    }

    // ==================== updateEmployee 测试 ====================

    @Test
    @DisplayName("更新员工 - 成功")
    void updateEmployee_success() {
        Employee employee = createEmployee(1L, "更新员工");
        when(employeeMapper.update(any(Employee.class))).thenReturn(true);

        ApiResponse response = employeeService.updateEmployee(employee);

        assertTrue(response.isSuccess());
        assertEquals("员工更新成功", response.getMessage());
    }

    @Test
    @DisplayName("更新员工 - 失败")
    void updateEmployee_fail() {
        Employee employee = createEmployee(999L, "更新员工");
        when(employeeMapper.update(any(Employee.class))).thenReturn(false);

        ApiResponse response = employeeService.updateEmployee(employee);

        assertFalse(response.isSuccess());
        assertEquals("员工更新失败", response.getMessage());
    }

    @Test
    @DisplayName("更新员工 - 异常")
    void updateEmployee_exception() {
        Employee employee = createEmployee(1L, "更新员工");
        when(employeeMapper.update(any(Employee.class))).thenThrow(new RuntimeException("约束冲突"));

        ApiResponse response = employeeService.updateEmployee(employee);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("约束冲突"));
    }

    // ==================== deleteEmployee 测试 ====================

    @Test
    @DisplayName("删除员工 - 成功")
    void deleteEmployee_success() {
        when(employeeMapper.delete(1L)).thenReturn(true);

        ApiResponse response = employeeService.deleteEmployee(1L);

        assertTrue(response.isSuccess());
        assertEquals("员工删除成功", response.getMessage());
    }

    @Test
    @DisplayName("删除员工 - 失败")
    void deleteEmployee_fail() {
        when(employeeMapper.delete(999L)).thenReturn(false);

        ApiResponse response = employeeService.deleteEmployee(999L);

        assertFalse(response.isSuccess());
        assertEquals("员工删除失败", response.getMessage());
    }

    @Test
    @DisplayName("删除员工 - 异常")
    void deleteEmployee_exception() {
        when(employeeMapper.delete(1L)).thenThrow(new RuntimeException("外键约束"));

        ApiResponse response = employeeService.deleteEmployee(1L);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("外键约束"));
    }

    // ==================== 辅助方法 ====================

    private Employee createEmployee(Long id, String name) {
        Employee e = new Employee();
        e.setId(id);
        e.setName(name);
        e.setGender("male");
        e.setPhone("13800000000");
        return e;
    }
}
