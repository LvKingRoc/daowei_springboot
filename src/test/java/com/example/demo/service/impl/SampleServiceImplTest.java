package com.example.demo.service.impl;

import com.example.demo.dto.DeleteResultDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.Sample;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.SampleMapper;
import com.example.demo.service.FileService;
import com.example.demo.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SampleServiceImpl 白盒测试
 * 覆盖样品 CRUD、图片处理、级联删除订单、fixNullCustomers 等全部分支
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SampleServiceImpl 白盒测试")
class SampleServiceImplTest {

    @InjectMocks
    private SampleServiceImpl sampleService;

    @Mock
    private SampleMapper sampleMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FileService fileService;

    @Mock
    private OperationLogService operationLogService;

    // ==================== getSamples 测试 ====================

    @Test
    @DisplayName("获取所有样品")
    void getSamples() {
        List<Sample> samples = Arrays.asList(createSample(1L), createSample(2L));
        when(sampleMapper.selectAll()).thenReturn(samples);

        List<Sample> result = sampleService.getSamples();

        assertEquals(2, result.size());
    }

    // ==================== getById 测试 ====================

    @Test
    @DisplayName("根据ID查询 - 存在")
    void getById_found() {
        Sample sample = createSample(1L);
        when(sampleMapper.selectById(1L)).thenReturn(sample);

        Sample result = sampleService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("根据ID查询 - 不存在")
    void getById_notFound() {
        when(sampleMapper.selectById(999L)).thenReturn(null);

        Sample result = sampleService.getById(999L);

        assertNull(result);
    }

    // ==================== save 测试 ====================

    @Test
    @DisplayName("保存样品 - 成功")
    void save_success() {
        Sample sample = createSample(null);
        doAnswer(invocation -> {
            Sample s = invocation.getArgument(0);
            s.setId(1L);
            return null;
        }).when(sampleMapper).insert(any(Sample.class));

        Long id = sampleService.save(sample);

        assertEquals(1L, id);
        verify(sampleMapper).insert(any(Sample.class));
    }

    // ==================== update 测试 ====================

    @Test
    @DisplayName("更新样品 - 成功")
    void update_success() {
        Sample sample = createSample(1L);
        when(sampleMapper.update(any(Sample.class))).thenReturn(1);

        boolean result = sampleService.update(sample);

        assertTrue(result);
    }

    @Test
    @DisplayName("更新样品 - 失败（影响行数为0）")
    void update_fail() {
        Sample sample = createSample(999L);
        when(sampleMapper.update(any(Sample.class))).thenReturn(0);

        boolean result = sampleService.update(sample);

        assertFalse(result);
    }

    // ==================== delete 测试 ====================

    @Test
    @DisplayName("删除样品 - 样品不存在")
    void delete_notFound() {
        when(sampleMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> sampleService.delete(999L));
        assertEquals("样品不存在", ex.getMessage());
    }

    @Test
    @DisplayName("删除样品 - 无关联订单、无图片")
    void delete_noOrdersNoImage() {
        Sample sample = createSample(1L);
        sample.setImage(null);
        when(sampleMapper.selectById(1L)).thenReturn(sample);
        when(orderMapper.listBySampleId(1L)).thenReturn(Collections.emptyList());

        DeleteResultDTO result = sampleService.delete(1L);

        assertNotNull(result);
        assertEquals(0, result.getAffectedCount());
        assertNull(result.getDescription());
        verify(sampleMapper).delete(1L);
        verify(fileService, never()).deleteFile(anyString());
    }

    @Test
    @DisplayName("删除样品 - 有关联订单（级联删除）")
    void delete_withOrders() {
        Sample sample = createSample(1L);
        sample.setImage(null);
        when(sampleMapper.selectById(1L)).thenReturn(sample);

        Order order1 = new Order();
        order1.setId(10L);
        order1.setSampleId(1L);
        Order order2 = new Order();
        order2.setId(11L);
        order2.setSampleId(1L);
        when(orderMapper.listBySampleId(1L)).thenReturn(Arrays.asList(order1, order2));

        DeleteResultDTO result = sampleService.delete(1L);

        assertEquals(2, result.getAffectedCount());
        assertTrue(result.getDescription().contains("连带删除2个关联订单"));
        verify(orderMapper).delete(10L);
        verify(orderMapper).delete(11L);
    }

    @Test
    @DisplayName("删除样品 - 有图片（删除图片文件）")
    void delete_withImage() {
        Sample sample = createSample(1L);
        sample.setImage("/sampleImage/test.jpg");
        when(sampleMapper.selectById(1L)).thenReturn(sample);
        when(orderMapper.listBySampleId(1L)).thenReturn(Collections.emptyList());

        sampleService.delete(1L);

        verify(fileService).deleteFile("/sampleImage/test.jpg");
    }

    // ==================== deleteImage 测试 ====================

    @Test
    @DisplayName("删除图片 - 样品不存在")
    void deleteImage_sampleNotFound() {
        when(sampleMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> sampleService.deleteImage(999L));
        assertEquals("样品不存在", ex.getMessage());
    }

    @Test
    @DisplayName("删除图片 - 样品没有图片")
    void deleteImage_noImage() {
        Sample sample = createSample(1L);
        sample.setImage(null);
        when(sampleMapper.selectById(1L)).thenReturn(sample);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> sampleService.deleteImage(1L));
        assertEquals("样品没有图片", ex.getMessage());
    }

    @Test
    @DisplayName("删除图片 - 删除文件失败")
    void deleteImage_deleteFileFailed() {
        Sample sample = createSample(1L);
        sample.setImage("/sampleImage/test.jpg");
        when(sampleMapper.selectById(1L)).thenReturn(sample);
        when(fileService.deleteFile("/sampleImage/test.jpg")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> sampleService.deleteImage(1L));
        assertEquals("删除图片文件失败", ex.getMessage());
    }

    @Test
    @DisplayName("删除图片 - 成功")
    void deleteImage_success() {
        Sample sample = createSample(1L);
        sample.setImage("/sampleImage/test.jpg");
        when(sampleMapper.selectById(1L)).thenReturn(sample);
        when(fileService.deleteFile("/sampleImage/test.jpg")).thenReturn(true);
        when(sampleMapper.update(any(Sample.class))).thenReturn(1);

        assertDoesNotThrow(() -> sampleService.deleteImage(1L));
        verify(fileService).deleteFile("/sampleImage/test.jpg");
    }

    // ==================== getOrderCount 测试 ====================

    @Test
    @DisplayName("获取样品关联订单数 - 有订单")
    void getOrderCount_hasOrders() {
        Order o1 = new Order();
        Order o2 = new Order();
        when(orderMapper.listBySampleId(1L)).thenReturn(Arrays.asList(o1, o2));

        int count = sampleService.getOrderCount(1L);

        assertEquals(2, count);
    }

    @Test
    @DisplayName("获取样品关联订单数 - null列表返回0")
    void getOrderCount_nullList() {
        when(orderMapper.listBySampleId(1L)).thenReturn(null);

        int count = sampleService.getOrderCount(1L);

        assertEquals(0, count);
    }

    // ==================== fixNullCustomers 测试 ====================

    @Test
    @DisplayName("修复空客户ID - 有需修复数据")
    void fixNullCustomers_hasData() {
        Sample s1 = createSample(1L);
        Sample s2 = createSample(2L);
        when(sampleMapper.findByNullCustomerId()).thenReturn(Arrays.asList(s1, s2));
        when(sampleMapper.updateCustomerId(anyLong(), eq(1L))).thenReturn(1);

        int count = sampleService.fixNullCustomers();

        assertEquals(2, count);
        verify(sampleMapper, times(2)).updateCustomerId(anyLong(), eq(1L));
    }

    @Test
    @DisplayName("修复空客户ID - 无需修复")
    void fixNullCustomers_noData() {
        when(sampleMapper.findByNullCustomerId()).thenReturn(Collections.emptyList());

        int count = sampleService.fixNullCustomers();

        assertEquals(0, count);
    }

    // ==================== 辅助方法 ====================

    private Sample createSample(Long id) {
        Sample sample = new Sample();
        sample.setId(id);
        sample.setCustomerId(1L);
        sample.setAlias("测试样品");
        sample.setModel("MODEL-001");
        sample.setStock(100);
        sample.setUnitPrice(new BigDecimal("9.99"));
        return sample;
    }
}
