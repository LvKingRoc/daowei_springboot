package com.example.demo.service.impl;

import com.example.demo.entity.Order;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OrderServiceImpl 白盒测试
 * 覆盖订单 CRUD 全部分支：正常流程、不存在、重复键、更新失败、删除失败
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl 白盒测试")
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderMapper orderMapper;

    // ==================== getById 测试 ====================

    @Test
    @DisplayName("根据ID查询 - 存在")
    void getById_found() {
        Order order = createOrder(1L, "ORD20260101001");
        when(orderMapper.getById(1L)).thenReturn(order);

        Order result = orderService.getById(1L);

        assertNotNull(result);
        assertEquals("ORD20260101001", result.getOrderNumber());
    }

    @Test
    @DisplayName("根据ID查询 - 不存在")
    void getById_notFound() {
        when(orderMapper.getById(999L)).thenReturn(null);

        Order result = orderService.getById(999L);

        assertNull(result);
    }

    // ==================== listByCondition 测试 ====================

    @Test
    @DisplayName("查询订单列表 - 有数据")
    void listByCondition_hasData() {
        List<Order> orders = Arrays.asList(
                createOrder(1L, "ORD001"),
                createOrder(2L, "ORD002")
        );
        when(orderMapper.listAll()).thenReturn(orders);

        List<Order> result = orderService.listByCondition();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("查询订单列表 - 空列表")
    void listByCondition_empty() {
        when(orderMapper.listAll()).thenReturn(Collections.emptyList());

        List<Order> result = orderService.listByCondition();

        assertTrue(result.isEmpty());
    }

    // ==================== save 测试 ====================

    @Test
    @DisplayName("保存订单 - 成功")
    void save_success() {
        Order order = createOrder(null, "ORD20260101001");
        // 模拟 insert 后 ID 被设置
        doAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1L);
            return null;
        }).when(orderMapper).insert(any(Order.class));
        when(orderMapper.getById(1L)).thenReturn(createOrder(1L, "ORD20260101001"));

        Order result = orderService.save(order);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderMapper).insert(any(Order.class));
    }

    @Test
    @DisplayName("保存订单 - 订单号重复抛出BusinessException")
    void save_duplicateKey() {
        Order order = createOrder(null, "ORD_DUP");
        doThrow(new DuplicateKeyException("duplicate")).when(orderMapper).insert(any(Order.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.save(order));
        assertTrue(ex.getMessage().contains("ORD_DUP"));
        assertTrue(ex.getMessage().contains("已存在"));
    }

    // ==================== update 测试 ====================

    @Test
    @DisplayName("更新订单 - 成功")
    void update_success() {
        Order order = createOrder(1L, "ORD001");
        when(orderMapper.getById(1L)).thenReturn(order);
        when(orderMapper.update(any(Order.class))).thenReturn(1);

        Order result = orderService.update(order);

        assertNotNull(result);
        verify(orderMapper).update(any(Order.class));
    }

    @Test
    @DisplayName("更新订单 - ID为null抛出异常")
    void update_nullId() {
        Order order = createOrder(null, "ORD001");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.update(order));
        assertEquals("订单ID不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("更新订单 - 订单不存在抛出异常")
    void update_notFound() {
        Order order = createOrder(999L, "ORD001");
        when(orderMapper.getById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.update(order));
        assertEquals("订单不存在", ex.getMessage());
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("更新订单 - 更新影响行数为0")
    void update_failedRows() {
        Order order = createOrder(1L, "ORD001");
        when(orderMapper.getById(1L)).thenReturn(order);
        when(orderMapper.update(any(Order.class))).thenReturn(0);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.update(order));
        assertEquals("订单更新失败", ex.getMessage());
    }

    // ==================== delete 测试 ====================

    @Test
    @DisplayName("删除订单 - 成功")
    void delete_success() {
        Order order = createOrder(1L, "ORD001");
        when(orderMapper.getById(1L)).thenReturn(order);
        when(orderMapper.delete(1L)).thenReturn(1);

        assertDoesNotThrow(() -> orderService.delete(1L));
        verify(orderMapper).delete(1L);
    }

    @Test
    @DisplayName("删除订单 - 订单不存在")
    void delete_notFound() {
        when(orderMapper.getById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.delete(999L));
        assertEquals("订单不存在", ex.getMessage());
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("删除订单 - 删除影响行数为0")
    void delete_failedRows() {
        Order order = createOrder(1L, "ORD001");
        when(orderMapper.getById(1L)).thenReturn(order);
        when(orderMapper.delete(1L)).thenReturn(0);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.delete(1L));
        assertEquals("订单删除失败", ex.getMessage());
    }

    // ==================== 辅助方法 ====================

    private Order createOrder(Long id, String orderNumber) {
        Order order = new Order();
        order.setId(id);
        order.setOrderNumber(orderNumber);
        order.setSampleId(1L);
        order.setTotalQuantity(100);
        order.setTotalAmount(new BigDecimal("9999.99"));
        order.setCreateDate(new Date());
        order.setDeliveryDate(new Date());
        order.setStatus("pending");
        return order;
    }
}
