package com.example.demo.mapper;

import com.example.demo.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    Employee findById(Integer id);
    List<Employee> findAll();
    boolean insert(Employee employee);
    boolean update(Employee employee);
    boolean delete(Integer id);
}