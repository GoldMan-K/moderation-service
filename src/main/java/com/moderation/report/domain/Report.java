package com.moderation.report.domain;

import com.moderation.global.exception.BusinessException;
import com.moderation.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String targetType;        // POST | COMMENT | MEETUP_CHAT
    private Long targetId;
    private Long reportedWriterMemberId;
    private Long reporterMemberId;    // null이면 비회원
    private String reporterIp;
    private String reasonCode;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private String status;            // PENDING | RESOLVED | REJECTED
    private Long handledByMemberId;
    private LocalDateTime handledAt;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDateTime createdAt;

    @Builder
    public Report(String targetType, Long targetId, Long reportedWriterMemberId,
                  Long reporterMemberId, String reporterIp, String reasonCode, String detail) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.reportedWriterMemberId = reportedWriterMemberId;
        this.reporterMemberId = reporterMemberId;
        this.reporterIp = reporterIp;
        this.reasonCode = reasonCode;
        this.detail = detail;
        this.status = "PENDING";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void validatePending() {
        if (!"PENDING".equals(this.status)) {
            throw new BusinessException(ErrorCode.REPORT_ALREADY_HANDLED);
        }
    }

    public void resolve(Long handledByMemberId, String memo) {
        this.status = "RESOLVED";
        this.handledByMemberId = handledByMemberId;
        this.handledAt = LocalDateTime.now();
        this.memo = memo;
    }

    public void reject(Long handledByMemberId, String memo) {
        this.status = "REJECTED";
        this.handledByMemberId = handledByMemberId;
        this.handledAt = LocalDateTime.now();
        this.memo = memo;
    }
}
