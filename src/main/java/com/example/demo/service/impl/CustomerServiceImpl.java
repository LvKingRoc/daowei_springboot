package com.example.demo.service.impl;

import com.example.demo.common.ApiResponse;
import com.example.demo.entity.*;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.SampleMapper;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户服务实现类
 * 实现CustomerService接口，提供客户信息管理相关的业务逻辑
 * 负责处理客户的增删改查及关联数据操作
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    
    /**
     * 客户数据访问对象
     * 用于对客户基本信息和关联数据进行CRUD操作
     */
    @Autowired
    private CustomerMapper customerMapper;
    
    /**
     * 订单数据访问对象
     * 用于查询与客户相关的订单信息
     */
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 样品数据访问对象
     * 用于查询与客户相关的样品信息
     */
    @Autowired
    private SampleMapper sampleMapper;

    /**
     * 根据ID获取客户详细信息
     * 包括客户基本信息、地址信息和联系人信息
     *
     * @param id 客户ID
     * @return 客户对象，如果不存在则返回null
     */
    @Override
    public Customer getById(Long id) {
        // 获取客户基本信息
        Customer customer = customerMapper.getById(id);
        if (customer != null) {
            // 加载客户的地址列表
            customer.setAddresses(customerMapper.getAddressesByCustomerId(id));
            // 加载客户的联系人列表
            customer.setContacts(customerMapper.getContactsByCustomerId(id));
        }
        return customer;
    }

    /**
     * 获取所有客户列表（按公司名称排序）
     *
     * @return 排序后的客户列表
     */
    @Override
    public List<Customer> listByCompanyName() {
        // 查询所有客户并按公司名称排序
        return customerMapper.selectAll();
    }

    /**
     * 保存客户信息
     * 根据客户ID是否存在，决定进行插入还是更新操作
     * 同时处理客户的地址和联系人信息
     *
     * @param customer 要保存的客户对象
     * @return 保存结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse save(Customer customer) {
        try {
            if (customer.getId() == null) {
                // 新客户，执行插入操作
                customerMapper.insert(customer);
                Long customerId = customer.getId();
                // 保存客户的关联数据（地址和联系人）
                saveCustomerDetails(customer, customerId);
                return ApiResponse.success("客户创建成功", customerId);
            } else {
                // 现有客户，执行更新操作
                customerMapper.update(customer);
                Long customerId = customer.getId();
                // 先删除旧的关联数据
                customerMapper.deleteAddressesByCustomerId(customerId);
                customerMapper.deleteContactsByCustomerId(customerId);
                // 保存新的关联数据
                saveCustomerDetails(customer, customerId);
                return ApiResponse.success("客户更新成功", customerId);
            }
        } catch (Exception e) {
            // 发生异常时回滚事务
            return ApiResponse.error("保存客户失败：" + e.getMessage());
        }
    }

    /**
     * 更新现有客户信息
     * 更新客户基本信息，并替换其所有地址和联系人
     *
     * @param customer 更新后的客户对象
     * @return 更新结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse update(Customer customer) {
        try {
            // 更新客户基本信息
            customerMapper.update(customer);
            Long customerId = customer.getId();
            // 先删除旧的关联数据
            customerMapper.deleteAddressesByCustomerId(customerId);
            customerMapper.deleteContactsByCustomerId(customerId);
            // 保存新的关联数据
            saveCustomerDetails(customer, customerId);
            return ApiResponse.success("客户更新成功", customerId);
        } catch (Exception e) {
            // 发生异常时回滚事务
            return ApiResponse.error("更新客户失败：" + e.getMessage());
        }
    }

    /**
     * 删除指定客户及其所有关联数据
     *
     * @param id 要删除的客户ID
     * @return 删除结果的API响应
     */
    @Override
    @Transactional
    public ApiResponse delete(Long id) {
        try {
            // 先删除客户的关联数据
            customerMapper.deleteAddressesByCustomerId(id);
            customerMapper.deleteContactsByCustomerId(id);
            // 删除客户基本信息
            customerMapper.delete(id);
            return ApiResponse.success("客户删除成功", id);
        } catch (Exception e) {
            // 发生异常时回滚事务
            return ApiResponse.error("删除客户失败：" + e.getMessage());
        }
    }

    /**
     * 保存客户的地址和联系人信息
     * 私有辅助方法，用于插入客户的关联数据
     *
     * @param customer 客户对象，包含地址和联系人列表
     * @param customerId 客户ID
     */
    private void saveCustomerDetails(Customer customer, Long customerId) {
        // 保存客户地址列表
        if (customer.getAddresses() != null) {
            for (CustomerAddress address : customer.getAddresses()) {
                address.setCustomerId(customerId);
                customerMapper.insertAddress(address);
            }
        }
        // 保存客户联系人列表
        if (customer.getContacts() != null) {
            for (CustomerContact contact : customer.getContacts()) {
                contact.setCustomerId(customerId);
                customerMapper.insertContact(contact);
            }
        }
    }

    /**
     * 获取指定客户的订单数量
     * 通过客户的公司名称查询相关订单
     *
     * @param customerId 客户ID
     * @return 该客户的订单数量
     */
    @Override
    public int getOrderCountByCustomerId(Long customerId) {
        // 先获取客户信息
        Customer customer = customerMapper.getById(customerId);
        if (customer == null) {
            return 0;
        }
        // 通过公司名称查询订单数量
        return orderMapper.countByCompanyName(customer.getCompanyName());
    }

    /**
     * 获取指定客户的样板数量
     *
     * @param customerId 客户ID
     * @return 该客户的样板数量
     */
    @Override
    public int getSampleCountByCustomerId(Long customerId) {
        // 查询指定客户ID关联的样品数量
        return sampleMapper.countByCustomerId(customerId);
    }
}