package com.moderation.supportinquiry.service;

import com.moderation.actionlog.domain.ModerationActionLog;
import com.moderation.actionlog.repository.ModerationActionLogRepository;
import com.moderation.global.exception.BusinessException;
import com.moderation.global.exception.ErrorCode;
import com.moderation.kafka.producer.ModerationEventProducer;
import com.moderation.supportinquiry.domain.SupportInquiry;
import com.moderation.supportinquiry.dto.CreateSupportInquiryRequest;
import com.moderation.supportinquiry.dto.SupportInquiryResponse;
import com.moderation.supportinquiry.dto.UpdateSupportInquiryStatusRequest;
import com.moderation.supportinquiry.repository.SupportInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupportInquiryService {

    private final SupportInquiryRepository supportInquiryRepository;
    private final ModerationActionLogRepository actionLogRepository;
    private final ModerationEventProducer eventProducer;

    @Transactional
    public SupportInquiryResponse createInquiry(Long memberId, CreateSupportInquiryRequest request) {
        requireMemberId(memberId);

        SupportInquiry inquiry = SupportInquiry.builder()
                .memberId(memberId)
                .title(request.title())
                .content(request.content())
                .build();

        supportInquiryRepository.save(inquiry);
        eventProducer.publishInquiryCreated(inquiry.getId(), inquiry.getMemberId(), inquiry.getTitle());

        return SupportInquiryResponse.from(inquiry);
    }

    public Page<SupportInquiryResponse> getMyInquiries(Long memberId, Pageable pageable) {
        requireMemberId(memberId);
        return supportInquiryRepository.findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId, pageable)
                .map(SupportInquiryResponse::from);
    }

    public SupportInquiryResponse getMyInquiry(Long memberId, Long inquiryId) {
        requireMemberId(memberId);
        SupportInquiry inquiry = supportInquiryRepository.findByIdAndMemberIdAndDeletedAtIsNull(inquiryId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPORT_INQUIRY_NOT_FOUND));
        return SupportInquiryResponse.from(inquiry);
    }

    @Transactional
    public void deleteMyInquiry(Long memberId, Long inquiryId) {
        requireMemberId(memberId);
        SupportInquiry inquiry = supportInquiryRepository.findByIdAndMemberIdAndDeletedAtIsNull(inquiryId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPORT_INQUIRY_NOT_FOUND));
        inquiry.markDeleted(memberId);
    }

    @Transactional
    public void deleteInquiryForAdmin(Long adminMemberId, Long inquiryId) {
        SupportInquiry inquiry = supportInquiryRepository.findByIdAndDeletedAtIsNull(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPORT_INQUIRY_NOT_FOUND));
        inquiry.markDeleted(adminMemberId);
    }

    public Page<SupportInquiryResponse> getInquiryListForAdmin(String status, Pageable pageable) {
        String normalizedStatus = normalizeStatus(status);
        return supportInquiryRepository.findAllByStatusFilter(normalizedStatus, pageable)
                .map(SupportInquiryResponse::from);
    }

    @Transactional
    public SupportInquiryResponse changeInquiryStatus(Long adminMemberId, Long inquiryId,
                                                      UpdateSupportInquiryStatusRequest request) {
        SupportInquiry inquiry = supportInquiryRepository.findByIdAndDeletedAtIsNull(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUPPORT_INQUIRY_NOT_FOUND));

        inquiry.changeStatus(request.status(), request.adminReply(), adminMemberId);

        actionLogRepository.save(ModerationActionLog.builder()
                .actorMemberId(adminMemberId)
                .actionType("INQUIRY_" + inquiry.getStatus())
                .targetType("SUPPORT_INQUIRY")
                .targetId(inquiry.getId())
                .reason(request.adminReply())
                .build());

        eventProducer.publishInquiryAnswered(
                inquiry.getId(),
                inquiry.getMemberId(),
                inquiry.getStatus(),
                inquiry.getAnsweredByMemberId()
        );

        return SupportInquiryResponse.from(inquiry);
    }

    private void requireMemberId(Long memberId) {
        if (memberId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    private String normalizeStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase();
    }
}


