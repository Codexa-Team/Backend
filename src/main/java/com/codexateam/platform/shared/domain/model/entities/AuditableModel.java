package com.codexateam.platform.shared.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Base abstract class for auditable models.
 * Automatically manages 'createdAt' and 'updatedAt' timestamps.
 * This class is intended to be extended by domain entities that are not aggregate roots.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableModel {

    /**
     * Timestamp indicating when the entity was created.
     * It is non-nullable and not updatable.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    /**
     * Timestamp indicating when the entity was last updated.
     * It is non-nullable.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;
}
