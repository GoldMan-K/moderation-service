package com.moderation.ipblock.dto;

import com.moderation.ipblock.domain.IpBlock;

import java.time.LocalDateTime;

public record IpBlockResponse(
        Long id,
        String ip,
        String reason,
        Long blockedByMemberId,
        LocalDateTime blockedAt,
        LocalDateTime expiresAt,
        boolean isPermanent
) {
    public static IpBlockResponse from(IpBlock b) {
        return new IpBlockResponse(
                b.getId(),
                b.getIp(),
                b.getReason(),
                b.getBlockedByMemberId(),
                b.getBlockedAt(),
                b.getExpiresAt(),
                b.isPermanent()
        );
    }
}
