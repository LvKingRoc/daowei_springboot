package com.example.demo.mapper;

import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerAddress;
import com.example.demo.entity.CustomerContact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {
    Customer getById(@Param("id") Long id);
    List<Customer> listByCompanyName(@Param("companyName") String companyName);
    int insert(Customer customer);
    int update(Customer customer);
    int delete(@Param("id") Long id);
    List<CustomerAddress> getAddressesByCustomerId(@Param("customerId") Long customerId);
    int insertAddress(CustomerAddress address);
    int deleteAddressesByCustomerId(@Param("customerId") Long customerId);
    List<CustomerContact> getContactsByCustomerId(@Param("customerId") Long customerId);
    int insertContact(CustomerContact contact);
    int deleteContactsByCustomerId(@Param("customerId") Long customerId);
    List<Customer> selectAll();
}