package com.example.demo.mapper;

import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerAddress;
import com.example.demo.entity.CustomerContact;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {
    Customer getById(Long id);
    List<Customer> listByCompanyName(String companyName);
    int insert(Customer customer);
    int update(Customer customer);
    int delete(Long id);
    List<CustomerAddress> getAddressesByCustomerId(Long customerId);
    int insertAddress(CustomerAddress address);
    int deleteAddressesByCustomerId(Long customerId);
    List<CustomerContact> getContactsByCustomerId(Long customerId);
    int insertContact(CustomerContact contact);
    int deleteContactsByCustomerId(Long customerId);
    List<Customer> selectAll();
}