package com.moderation.report.dto;

import com.moderation.report.domain.Report;

import java.time.LocalDateTime;

public record ReportResponse(
        Long id,
        String targetType,
        Long targetId,
        Long reportedWriterMemberId,
        Long reporterMemberId,
        String reasonCode,
        String detail,
        String status,
        Long handledByMemberId,
        LocalDateTime handledAt,
        String memo,
        LocalDateTime createdAt
) {
    public static ReportResponse from(Report r) {
        return new ReportResponse(
                r.getId(),
                r.getTargetType(),
                r.getTargetId(),
                r.getReportedWriterMemberId(),
                r.getReporterMemberId(),
                r.getReasonCode(),
                r.getDetail(),
                r.getStatus(),
                r.getHandledByMemberId(),
                r.getHandledAt(),
                r.getMemo(),
                r.getCreatedAt()
        );
    }
}
