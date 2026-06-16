package com.thh.kiosk.queue.core.model.entity;

import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PROTECTED)
@SoftDelete(columnName = "is_deleted")
public abstract class BaseSqlEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    String updatedBy;

    @Column(name = "is_deleted", nullable = false, insertable = false, updatable = false)
    Boolean isDeleted = false;

    @Column(name = "deleted_at")
    Instant deletedAt;

    @Column(name = "deleted_by")
    String deletedBy;

    public void markAsDeleted(String deletedBy) {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
    }
}
