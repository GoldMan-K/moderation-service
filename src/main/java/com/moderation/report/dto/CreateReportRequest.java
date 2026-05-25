package com.moderation.report.dto;

public record CreateReportRequest(
        String targetType,   // POST | COMMENT | MEETUP_CHAT
        Long targetId,
        Long reportedWriterMemberId,
        String reasonCode,
        String detail
) {}
