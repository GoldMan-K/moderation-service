package com.moderation.actionlog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "moderation_action_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModerationActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long actorMemberId;
    private String actionType;   // REPORT_RESOLVE, REPORT_REJECT, IP_BLOCK, IP_UNBLOCK 등
    private String targetType;
    private Long targetId;
    private String reason;

    @Column(columnDefinition = "JSON")
    private String metaJson;     // 이전값/이후값 등 추가 데이터

    private LocalDateTime createdAt;

    @Builder
    public ModerationActionLog(Long actorMemberId, String actionType,
                                String targetType, Long targetId,
                                String reason, String metaJson) {
        this.actorMemberId = actorMemberId;
        this.actionType = actionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.metaJson = metaJson;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
