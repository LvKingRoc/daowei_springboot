package com.example.demo.service.impl;

import com.example.demo.dto.DeleteResultDTO;
import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerAddress;
import com.example.demo.entity.CustomerContact;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.SampleMapper;
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
 * CustomerServiceImpl 白盒测试
 * 覆盖客户 CRUD、关联数据处理、级联删除等全部分支路径
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl 白盒测试")
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SampleMapper sampleMapper;

    // ==================== getById 测试 ====================

    @Test
    @DisplayName("根据ID查询 - 存在（加载地址和联系人）")
    void getById_found() {
        Customer customer = createCustomer(1L, "测试公司");
        when(customerMapper.getById(1L)).thenReturn(customer);
        when(customerMapper.getAddressesByCustomerId(1L)).thenReturn(Collections.emptyList());
        when(customerMapper.getContactsByCustomerId(1L)).thenReturn(Collections.emptyList());

        Customer result = customerService.getById(1L);

        assertNotNull(result);
        assertEquals("测试公司", result.getCompanyName());
        verify(customerMapper).getAddressesByCustomerId(1L);
        verify(customerMapper).getContactsByCustomerId(1L);
    }

    @Test
    @DisplayName("根据ID查询 - 不存在（不加载关联数据）")
    void getById_notFound() {
        when(customerMapper.getById(999L)).thenReturn(null);

        Customer result = customerService.getById(999L);

        assertNull(result);
        verify(customerMapper, never()).getAddressesByCustomerId(anyLong());
        verify(customerMapper, never()).getContactsByCustomerId(anyLong());
    }

    // ==================== save 测试 ====================

    @Test
    @DisplayName("保存客户 - 有地址和联系人")
    void save_withDetails() {
        Customer customer = createCustomer(null, "新公司");
        CustomerAddress addr = new CustomerAddress();
        addr.setAddress("北京市朝阳区");
        customer.setAddresses(Arrays.asList(addr));
        CustomerContact contact = new CustomerContact();
        contact.setContactName("张三");
        contact.setPhone("13800000000");
        customer.setContacts(Arrays.asList(contact));

        doAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(1L);
            return null;
        }).when(customerMapper).insert(any(Customer.class));

        when(customerMapper.getById(1L)).thenReturn(createCustomer(1L, "新公司"));
        when(customerMapper.getAddressesByCustomerId(1L)).thenReturn(Collections.emptyList());
        when(customerMapper.getContactsByCustomerId(1L)).thenReturn(Collections.emptyList());

        Customer result = customerService.save(customer);

        assertNotNull(result);
        verify(customerMapper).insertAddress(any(CustomerAddress.class));
        verify(customerMapper).insertContact(any(CustomerContact.class));
    }

    @Test
    @DisplayName("保存客户 - 无地址和联系人（null列表）")
    void save_withoutDetails() {
        Customer customer = createCustomer(null, "简单公司");
        customer.setAddresses(null);
        customer.setContacts(null);

        doAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(2L);
            return null;
        }).when(customerMapper).insert(any(Customer.class));

        when(customerMapper.getById(2L)).thenReturn(createCustomer(2L, "简单公司"));
        when(customerMapper.getAddressesByCustomerId(2L)).thenReturn(Collections.emptyList());
        when(customerMapper.getContactsByCustomerId(2L)).thenReturn(Collections.emptyList());

        Customer result = customerService.save(customer);

        assertNotNull(result);
        verify(customerMapper, never()).insertAddress(any(CustomerAddress.class));
        verify(customerMapper, never()).insertContact(any(CustomerContact.class));
    }

    // ==================== update 测试 ====================

    @Test
    @DisplayName("更新客户 - ID为null抛出异常")
    void update_nullId() {
        Customer customer = createCustomer(null, "公司");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.update(customer));
        assertEquals("客户ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("更新客户 - 客户不存在抛出异常")
    void update_notFound() {
        Customer customer = createCustomer(999L, "公司");
        when(customerMapper.getById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.update(customer));
        assertEquals("客户不存在", ex.getMessage());
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("更新客户 - 成功（先删旧关联再插新关联）")
    void update_success() {
        Customer customer = createCustomer(1L, "更新公司");
        customer.setAddresses(Collections.emptyList());
        customer.setContacts(Collections.emptyList());

        when(customerMapper.getById(1L)).thenReturn(createCustomer(1L, "旧公司"));
        when(customerMapper.getAddressesByCustomerId(1L)).thenReturn(Collections.emptyList());
        when(customerMapper.getContactsByCustomerId(1L)).thenReturn(Collections.emptyList());

        customerService.update(customer);

        verify(customerMapper).update(any(Customer.class));
        verify(customerMapper).deleteAddressesByCustomerId(1L);
        verify(customerMapper).deleteContactsByCustomerId(1L);
    }

    // ==================== delete 测试 ====================

    @Test
    @DisplayName("删除客户 - 客户不存在")
    void delete_notFound() {
        when(customerMapper.getById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.delete(999L));
        assertEquals("客户不存在", ex.getMessage());
    }

    @Test
    @DisplayName("删除客户 - 无关联样品")
    void delete_noSamples() {
        when(customerMapper.getById(1L)).thenReturn(createCustomer(1L, "公司"));
        when(sampleMapper.countByCustomerId(1L)).thenReturn(0);

        DeleteResultDTO result = customerService.delete(1L);

        assertNotNull(result);
        assertEquals(0, result.getAffectedCount());
        assertNull(result.getDescription());
        verify(sampleMapper, never()).updateCustomerIdByOldCustomerId(anyLong(), anyLong());
        verify(customerMapper).delete(1L);
    }

    @Test
    @DisplayName("删除客户 - 有关联样品（转为无客户状态）")
    void delete_withSamples() {
        when(customerMapper.getById(1L)).thenReturn(createCustomer(1L, "公司"));
        when(sampleMapper.countByCustomerId(1L)).thenReturn(3);

        DeleteResultDTO result = customerService.delete(1L);

        assertEquals(3, result.getAffectedCount());
        assertTrue(result.getDescription().contains("3个关联样品"));
        verify(sampleMapper).updateCustomerIdByOldCustomerId(1L, 2L);
        verify(customerMapper).delete(1L);
    }

    // ==================== getOrderCountByCustomerId 测试 ====================

    @Test
    @DisplayName("获取客户订单数 - 客户不存在返回0")
    void getOrderCount_customerNotFound() {
        when(customerMapper.getById(999L)).thenReturn(null);

        int count = customerService.getOrderCountByCustomerId(999L);

        assertEquals(0, count);
        verify(orderMapper, never()).countByCompanyName(anyString());
    }

    @Test
    @DisplayName("获取客户订单数 - 正常返回")
    void getOrderCount_normal() {
        Customer customer = createCustomer(1L, "测试公司");
        when(customerMapper.getById(1L)).thenReturn(customer);
        when(orderMapper.countByCompanyName("测试公司")).thenReturn(5);

        int count = customerService.getOrderCountByCustomerId(1L);

        assertEquals(5, count);
    }

    // ==================== getSampleCountByCustomerId 测试 ====================

    @Test
    @DisplayName("获取客户样品数")
    void getSampleCount() {
        when(sampleMapper.countByCustomerId(1L)).thenReturn(10);

        int count = customerService.getSampleCountByCustomerId(1L);

        assertEquals(10, count);
    }

    // ==================== 辅助方法 ====================

    private Customer createCustomer(Long id, String companyName) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setCompanyName(companyName);
        return customer;
    }
}
