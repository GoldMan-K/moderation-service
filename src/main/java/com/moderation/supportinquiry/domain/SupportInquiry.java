package com.moderation.supportinquiry.domain;

import com.moderation.global.exception.BusinessException;
import com.moderation.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_inquiry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @Column(length = 120)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String adminReply;

    private Long answeredByMemberId;
    private LocalDateTime answeredAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
    private Long deletedByMemberId;

    @Builder
    public SupportInquiry(Long memberId, String title, String content) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.status = "RECEIVED";
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.status == null) {
            this.status = "RECEIVED";
        }
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(String status, String adminReply, Long adminMemberId) {
        String nextStatus = normalizeStatus(status);
        if (!"RECEIVED".equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPORT_INQUIRY_ALREADY_HANDLED);
        }
        if (!"ANSWERED".equals(nextStatus) && !"CLOSED".equals(nextStatus)) {
            throw new BusinessException(ErrorCode.SUPPORT_INQUIRY_INVALID_STATUS);
        }
        if (adminReply == null || adminReply.isBlank()) {
            throw new BusinessException(ErrorCode.SUPPORT_INQUIRY_REPLY_REQUIRED);
        }

        this.status = nextStatus;
        this.adminReply = adminReply;
        this.answeredByMemberId = adminMemberId;
        this.answeredAt = LocalDateTime.now();
    }

    public void markDeleted(Long deletedByMemberId) {
        if (this.deletedAt != null) {
            return;
        }
        this.deletedAt = LocalDateTime.now();
        this.deletedByMemberId = deletedByMemberId;
    }

    private String normalizeStatus(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase();
    }
}

