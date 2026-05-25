package com.moderation.ipblock.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ip_block")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IpBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ip;
    private String reason;
    private Long blockedByMemberId;
    private LocalDateTime blockedAt;
    private LocalDateTime expiresAt; // null이면 영구 차단

    @Builder
    public IpBlock(String ip, String reason, Long blockedByMemberId, LocalDateTime expiresAt) {
        this.ip = ip;
        this.reason = reason;
        this.blockedByMemberId = blockedByMemberId;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    protected void onCreate() {
        this.blockedAt = LocalDateTime.now();
    }

    public void updateExpiry(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isPermanent() {
        return this.expiresAt == null;
    }

    public boolean isExpired() {
        return this.expiresAt != null && this.expiresAt.isBefore(LocalDateTime.now());
    }
}
