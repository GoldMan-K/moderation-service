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
    private static final String TOPIC_INQUIRY_CREATED = "inquiry.created";
    private static final String TOPIC_INQUIRY_ANSWERED = "inquiry.answered";

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

    /**
     * 문의 접수 이벤트 발행
     * - Notification: 사용자 접수 알림 또는 관리자 알림 생성
     */
    public void publishInquiryCreated(Long inquiryId, Long memberId, String title) {
        Map<String, Object> payload = Map.of(
                "inquiryId", inquiryId,
                "memberId", memberId != null ? memberId : 0L,
                "title", title != null ? title : ""
        );
        kafkaTemplate.send(TOPIC_INQUIRY_CREATED, String.valueOf(inquiryId), payload);
        log.info("[Kafka] inquiry.created published: inquiryId={}, memberId={}", inquiryId, memberId);
    }

    /**
     * 문의 답변 완료 이벤트 발행
     * - Notification: 작성자에게 답변 완료 알림 생성
     */
    public void publishInquiryAnswered(Long inquiryId, Long memberId, String status, Long answeredByMemberId) {
        Map<String, Object> payload = Map.of(
                "inquiryId", inquiryId,
                "memberId", memberId != null ? memberId : 0L,
                "status", status != null ? status : "ANSWERED",
                "answeredByMemberId", answeredByMemberId != null ? answeredByMemberId : 0L
        );
        kafkaTemplate.send(TOPIC_INQUIRY_ANSWERED, String.valueOf(inquiryId), payload);
        log.info("[Kafka] inquiry.answered published: inquiryId={}, memberId={}, status={}",
                inquiryId, memberId, status);
    }
}
