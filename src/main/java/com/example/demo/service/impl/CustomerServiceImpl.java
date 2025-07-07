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
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private SampleMapper sampleMapper;

    @Override
    public Customer getById(Long id) {
        Customer customer = customerMapper.getById(id);
        if (customer != null) {
            customer.setAddresses(customerMapper.getAddressesByCustomerId(id));
            customer.setContacts(customerMapper.getContactsByCustomerId(id));
        }
        return customer;
    }

    @Override
    public List<Customer> listByCompanyName() {
        return customerMapper.selectAll();
    }

    @Override
    @Transactional
    public ApiResponse save(Customer customer) {
        try {
            if (customer.getId() == null) {
                customerMapper.insert(customer);
                Long customerId = customer.getId();
                saveCustomerDetails(customer, customerId);
                return ApiResponse.success("客户创建成功", customerId);
            } else {
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
            return ApiResponse.error("保存客户失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse update(Customer customer) {
        try {
            customerMapper.update(customer);
            Long customerId = customer.getId();
            // 先删除旧的关联数据
            customerMapper.deleteAddressesByCustomerId(customerId);
            customerMapper.deleteContactsByCustomerId(customerId);
            // 保存新的关联数据
            saveCustomerDetails(customer, customerId);
            return ApiResponse.success("客户更新成功", customerId);
        } catch (Exception e) {
            return ApiResponse.error("更新客户失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse delete(Long id) {
        try {
            customerMapper.deleteAddressesByCustomerId(id);
            customerMapper.deleteContactsByCustomerId(id);
            customerMapper.delete(id);
            return ApiResponse.success("客户删除成功", id);
        } catch (Exception e) {
            return ApiResponse.error("删除客户失败：" + e.getMessage());
        }
    }

    private void saveCustomerDetails(Customer customer, Long customerId) {
        if (customer.getAddresses() != null) {
            for (CustomerAddress address : customer.getAddresses()) {
                address.setCustomerId(customerId);
                customerMapper.insertAddress(address);
            }
        }
        if (customer.getContacts() != null) {
            for (CustomerContact contact : customer.getContacts()) {
                contact.setCustomerId(customerId);
                customerMapper.insertContact(contact);
            }
        }
    }

    @Override
    public int getOrderCountByCustomerId(Long customerId) {
        Customer customer = customerMapper.getById(customerId);
        if (customer == null) {
            return 0;
        }
        return orderMapper.countByCompanyName(customer.getCompanyName());
    }

    @Override
    public int getSampleCountByCustomerId(Long customerId) {
        return sampleMapper.countByCustomerId(customerId);
    }
}