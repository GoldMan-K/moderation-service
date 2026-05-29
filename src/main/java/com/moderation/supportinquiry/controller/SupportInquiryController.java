package com.moderation.supportinquiry.controller;

import com.moderation.supportinquiry.dto.CreateSupportInquiryRequest;
import com.moderation.supportinquiry.dto.SupportInquiryResponse;
import com.moderation.supportinquiry.dto.UpdateSupportInquiryStatusRequest;
import com.moderation.supportinquiry.service.SupportInquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SupportInquiry", description = "문의 API")
@RestController
@RequestMapping("/api/moderation/support-inquiries")
@RequiredArgsConstructor
public class SupportInquiryController {

    private final SupportInquiryService supportInquiryService;

    @Operation(summary = "문의 등록", description = "사용자가 문의를 등록합니다.")
    @PostMapping
    public ResponseEntity<SupportInquiryResponse> createInquiry(
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody CreateSupportInquiryRequest request
    ) {
        return ResponseEntity.ok(supportInquiryService.createInquiry(memberId, request));
    }

    @Operation(summary = "내 문의 목록 조회", description = "삭제되지 않은 본인 문의 목록을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<Page<SupportInquiryResponse>> getMyInquiries(
            @RequestHeader("X-Member-Id") Long memberId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(supportInquiryService.getMyInquiries(memberId, pageable));
    }

    @Operation(summary = "내 문의 상세 조회", description = "삭제되지 않은 본인 문의를 단건 조회합니다.")
    @GetMapping("/me/{inquiryId}")
    public ResponseEntity<SupportInquiryResponse> getMyInquiry(
            @RequestHeader("X-Member-Id") Long memberId,
            @PathVariable Long inquiryId
    ) {
        return ResponseEntity.ok(supportInquiryService.getMyInquiry(memberId, inquiryId));
    }

    @Operation(summary = "내 문의 삭제", description = "본인 문의를 소프트 삭제합니다.")
    @DeleteMapping("/me/{inquiryId}")
    public ResponseEntity<Void> deleteMyInquiry(
            @RequestHeader("X-Member-Id") Long memberId,
            @PathVariable Long inquiryId
    ) {
        supportInquiryService.deleteMyInquiry(memberId, inquiryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[ADMIN] 문의 목록 조회", description = "상태 필터로 문의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<SupportInquiryResponse>> getInquiriesForAdmin(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(supportInquiryService.getInquiryListForAdmin(status, pageable));
    }

    @Operation(summary = "[ADMIN] 문의 상태 변경 및 답변", description = "RECEIVED 문의를 ANSWERED 또는 CLOSED로 변경하고 답변을 저장합니다.")
    @PatchMapping("/{inquiryId}/status")
    public ResponseEntity<SupportInquiryResponse> changeInquiryStatus(
            @RequestHeader("X-Member-Id") Long adminMemberId,
            @PathVariable Long inquiryId,
            @RequestBody UpdateSupportInquiryStatusRequest request
    ) {
        return ResponseEntity.ok(supportInquiryService.changeInquiryStatus(adminMemberId, inquiryId, request));
    }

    @Operation(summary = "[ADMIN] 문의 삭제", description = "운영 정책에 따라 문의를 소프트 삭제합니다.")
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteInquiryForAdmin(
            @RequestHeader("X-Member-Id") Long adminMemberId,
            @PathVariable Long inquiryId
    ) {
        supportInquiryService.deleteInquiryForAdmin(adminMemberId, inquiryId);
        return ResponseEntity.noContent().build();
    }
}


