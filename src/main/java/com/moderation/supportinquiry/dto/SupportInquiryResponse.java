package com.moderation.supportinquiry.dto;

import com.moderation.supportinquiry.domain.SupportInquiry;

import java.time.LocalDateTime;

public record SupportInquiryResponse(
        Long id,
        Long memberId,
        String title,
        String content,
        String status,
        String adminReply,
        Long answeredByMemberId,
        LocalDateTime answeredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static SupportInquiryResponse from(SupportInquiry inquiry) {
        return new SupportInquiryResponse(
                inquiry.getId(),
                inquiry.getMemberId(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getStatus(),
                inquiry.getAdminReply(),
                inquiry.getAnsweredByMemberId(),
                inquiry.getAnsweredAt(),
                inquiry.getCreatedAt(),
                inquiry.getUpdatedAt()
        );
    }
}

