package com.moderation.supportinquiry.dto;

public record UpdateSupportInquiryStatusRequest(
        String status,
        String adminReply
) {
}

