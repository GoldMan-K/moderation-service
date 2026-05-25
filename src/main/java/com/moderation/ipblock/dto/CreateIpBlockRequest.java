package com.moderation.ipblock.dto;

import java.time.LocalDateTime;

public record CreateIpBlockRequest(
        String ip,
        String reason,
        LocalDateTime expiresAt  // null이면 영구 차단
) {}
