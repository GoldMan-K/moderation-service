package com.moderation.kafka.consumer;

import com.moderation.keyword.service.ProfanityKeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommunityEventConsumer {

    private final ProfanityKeywordService profanityKeywordService;

    /**
     * post.created 이벤트 소비 — 자동 욕설·스팸 필터링
     */
    @KafkaListener(topics = "post.created", groupId = "moderation-service-group")
    public void handlePostCreated(Map<String, Object> payload) {
        try {
            Long postId = Long.valueOf(payload.get("postId").toString());
            String title = payload.getOrDefault("title", "").toString();
            String content = payload.getOrDefault("content", "").toString();

            log.info("[Kafka] post.created consumed: postId={}", postId);
            profanityKeywordService.checkAndFilter(postId, title + " " + content);
        } catch (Exception e) {
            log.error("[Kafka] post.created 처리 실패: {}", e.getMessage(), e);
        }
    }
}
