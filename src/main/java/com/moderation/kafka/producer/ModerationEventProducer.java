package com.moderation.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_REPORT_CREATED  = "report.created";
    private static final String TOPIC_REPORT_RESOLVED = "report.resolved";

    /**
     * 신고 접수 이벤트 발행
     * - Notification: 관리자에게 알림
     * - 임계값 초과 시 자동 숨김 처리 (소비자 측 구현)
     */
    public void publishReportCreated(Long reportId, String targetType, Long targetId,
                                     Long reportedWriterMemberId, Long reporterMemberId) {
        Map<String, Object> payload = Map.of(
                "reportId", reportId,
                "targetType", targetType,
                "targetId", targetId,
                "reportedWriterMemberId", reportedWriterMemberId != null ? reportedWriterMemberId : 0L,
                "reporterMemberId", reporterMemberId != null ? reporterMemberId : 0L
        );
        kafkaTemplate.send(TOPIC_REPORT_CREATED, String.valueOf(reportId), payload);
        log.info("[Kafka] report.created published: reportId={}, targetType={}, targetId={}",
                reportId, targetType, targetId);
    }

    /**
     * 신고 처리 완료 이벤트 발행
     * - Notification: 신고자·피신고자에게 처리 결과 알림
     */
    public void publishReportResolved(Long reportId, String status,
                                      Long reporterMemberId, Long reportedWriterMemberId) {
        Map<String, Object> payload = Map.of(
                "reportId", reportId,
                "status", status,
                "reporterMemberId", reporterMemberId != null ? reporterMemberId : 0L,
                "reportedWriterMemberId", reportedWriterMemberId != null ? reportedWriterMemberId : 0L
        );
        kafkaTemplate.send(TOPIC_REPORT_RESOLVED, String.valueOf(reportId), payload);
        log.info("[Kafka] report.resolved published: reportId={}, status={}", reportId, status);
    }
}
