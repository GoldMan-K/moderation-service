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
public class ConfigEventConsumer {

    private final ProfanityKeywordService profanityKeywordService;

    /**
     * config.taxonomy.changed 이벤트 소비 — 로컬 키워드 캐시 무효화
     */
    @KafkaListener(topics = "config.taxonomy.changed", groupId = "moderation-service-group")
    public void handleTaxonomyChanged(Map<String, Object> payload) {
        try {
            log.info("[Kafka] config.taxonomy.changed consumed — 키워드 캐시 갱신");
            profanityKeywordService.refreshCache();
        } catch (Exception e) {
            log.error("[Kafka] config.taxonomy.changed 처리 실패: {}", e.getMessage(), e);
        }
    }
}
