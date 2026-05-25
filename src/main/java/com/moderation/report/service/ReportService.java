package com.moderation.report.service;

import com.moderation.actionlog.domain.ModerationActionLog;
import com.moderation.actionlog.repository.ModerationActionLogRepository;
import com.moderation.global.exception.BusinessException;
import com.moderation.global.exception.ErrorCode;
import com.moderation.kafka.producer.ModerationEventProducer;
import com.moderation.report.domain.Report;
import com.moderation.report.dto.CreateReportRequest;
import com.moderation.report.dto.HandleReportRequest;
import com.moderation.report.dto.ReportResponse;
import com.moderation.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final ModerationActionLogRepository actionLogRepository;
    private final ModerationEventProducer eventProducer;

    // ─── 신고 접수 ───────────────────────────────────────────────────────────

    @Transactional
    public ReportResponse createReport(Long reporterMemberId, String reporterIp,
                                       CreateReportRequest request) {
        // 동일 대상 중복 신고 방지 (회원만)
        if (reporterMemberId != null &&
                reportRepository.existsByTargetTypeAndTargetIdAndReporterMemberId(
                        request.targetType(), request.targetId(), reporterMemberId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REPORT);
        }

        Report report = Report.builder()
                .targetType(request.targetType())
                .targetId(request.targetId())
                .reportedWriterMemberId(request.reportedWriterMemberId())
                .reporterMemberId(reporterMemberId)
                .reporterIp(reporterIp)
                .reasonCode(request.reasonCode())
                .detail(request.detail())
                .build();
        reportRepository.save(report);

        // report.created 이벤트 발행
        eventProducer.publishReportCreated(
                report.getId(), report.getTargetType(), report.getTargetId(),
                report.getReportedWriterMemberId(), report.getReporterMemberId()
        );

        return ReportResponse.from(report);
    }

    // ─── 신고 목록 조회 [ADMIN] ──────────────────────────────────────────────

    public Page<ReportResponse> getReportList(String status, String targetType, Pageable pageable) {
        return reportRepository.findAllByFilter(status, targetType, pageable)
                .map(ReportResponse::from);
    }

    // ─── 신고 처리 [ADMIN] ───────────────────────────────────────────────────

    @Transactional
    public ReportResponse handleReport(Long adminMemberId, Long reportId,
                                       HandleReportRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));
        report.validatePending();

        if ("RESOLVED".equals(request.status())) {
            report.resolve(adminMemberId, request.memo());
        } else {
            report.reject(adminMemberId, request.memo());
        }

        // 관리자 액션 로그 (INSERT only — 불변 감사 로그)
        actionLogRepository.save(ModerationActionLog.builder()
                .actorMemberId(adminMemberId)
                .actionType("REPORT_" + request.status())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(request.memo())
                .build());

        // report.resolved 이벤트 발행
        eventProducer.publishReportResolved(
                reportId, request.status(),
                report.getReporterMemberId(), report.getReportedWriterMemberId()
        );

        return ReportResponse.from(report);
    }
}
