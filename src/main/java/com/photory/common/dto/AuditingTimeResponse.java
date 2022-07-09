package com.photory.common.dto;

import com.photory.common.util.DateUtil;
import com.photory.domain.common.AuditingTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AuditingTimeResponse {

    protected long createdAt;
    protected long updatedAt;

    protected void setBaseTime(AuditingTimeEntity auditingTimeEntity) {
        this.createdAt = DateUtil.convertToTimeInterval(auditingTimeEntity.getCreatedAt());
        this.updatedAt = DateUtil.convertToTimeInterval(auditingTimeEntity.getUpdatedAt());
    }

    protected void setBaseTime(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = DateUtil.convertToTimeInterval(createdAt);
        this.updatedAt = DateUtil.convertToTimeInterval(updatedAt);
    }
}
