package com.example.demo.mapper;

import com.example.demo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    Employee findById(@Param("id") Integer id);
    List<Employee> findAll();
    boolean insert(Employee employee);
    boolean update(Employee employee);
    boolean delete(@Param("id") Integer id);
}