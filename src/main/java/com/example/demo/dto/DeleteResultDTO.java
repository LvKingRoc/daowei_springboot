package com.example.demo.dto;

import lombok.Data;

/**
 * 删除操作结果DTO
 * 用于返回删除操作的详细结果
 */
@Data
public class DeleteResultDTO {
    
    /**
     * 被删除的ID
     */
    private Long deletedId;
    
    /**
     * 影响的关联数据数量
     */
    private int affectedCount;
    
    /**
     * 结果描述
     */
    private String description;
    
    public DeleteResultDTO() {}
    
    public DeleteResultDTO(Long deletedId) {
        this.deletedId = deletedId;
        this.affectedCount = 0;
    }
    
    public DeleteResultDTO(Long deletedId, int affectedCount, String description) {
        this.deletedId = deletedId;
        this.affectedCount = affectedCount;
        this.description = description;
    }
    
    /**
     * 创建简单删除结果
     */
    public static DeleteResultDTO of(Long id) {
        return new DeleteResultDTO(id);
    }
    
    /**
     * 创建带关联数据的删除结果
     */
    public static DeleteResultDTO of(Long id, int affectedCount, String description) {
        return new DeleteResultDTO(id, affectedCount, description);
    }
}
