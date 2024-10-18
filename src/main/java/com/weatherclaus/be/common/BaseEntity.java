package com.weatherclaus.be.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate  // 엔티티가 처음 생성될 때 자동으로 시간 기록
    @Column(updatable = false)  // 생성일자는 수정되지 않도록 설정
    private LocalDateTime createdAt;

    @LastModifiedDate  // 엔티티가 업데이트될 때 자동으로 시간 기록
    private LocalDateTime updatedAt;
}