package com.moderation.ipblock.dto;

import java.time.LocalDateTime;

public record UpdateIpBlockRequest(
        LocalDateTime expiresAt  // null이면 영구 차단으로 변경
) {}
