package com.moderation.report.dto;

public record HandleReportRequest(
        String status,  // RESOLVED | REJECTED
        String memo
) {}
