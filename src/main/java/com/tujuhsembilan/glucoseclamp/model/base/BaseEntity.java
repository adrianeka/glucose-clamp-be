package com.tujuhsembilan.glucoseclamp.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    protected EntityStatus status = EntityStatus.ACTIVE;
    
    @Column(name = "created_at")
    protected LocalDateTime createdAt;
    
    @Column(name = "created_by")
    protected Integer createdBy;
    
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;
    
    @Column(name = "updated_by")
    protected Integer updatedBy;
    
    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;
    
    @Column(name = "deleted_by")
    protected Integer deletedBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = EntityStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
