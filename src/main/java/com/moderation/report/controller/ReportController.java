package com.moderation.report.controller;

import com.moderation.report.dto.CreateReportRequest;
import com.moderation.report.dto.HandleReportRequest;
import com.moderation.report.dto.ReportResponse;
import com.moderation.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report", description = "신고 API")
@RestController
@RequestMapping("/api/moderation/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "신고 접수", description = "콘텐츠를 신고합니다. 동일 대상 중복 신고는 409를 반환합니다.")
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId,
            HttpServletRequest httpRequest,
            @RequestBody CreateReportRequest request
    ) {
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(reportService.createReport(memberId, clientIp, request));
    }

    @Operation(summary = "[ADMIN] 신고 목록 조회", description = "상태·타입 필터로 신고 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getReportList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String targetType,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getReportList(status, targetType, pageable));
    }

    @Operation(summary = "[ADMIN] 신고 처리", description = "신고를 RESOLVED 또는 REJECTED로 처리합니다. 액션 로그가 기록됩니다.")
    @PatchMapping("/{reportId}/status")
    public ResponseEntity<ReportResponse> handleReport(
            @RequestHeader("X-Member-Id") Long adminMemberId,
            @PathVariable Long reportId,
            @RequestBody HandleReportRequest request
    ) {
        return ResponseEntity.ok(reportService.handleReport(adminMemberId, reportId, request));
    }
}
