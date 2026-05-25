package com.moderation.ipblock.controller;

import com.moderation.ipblock.dto.CreateIpBlockRequest;
import com.moderation.ipblock.dto.IpBlockResponse;
import com.moderation.ipblock.dto.UpdateIpBlockRequest;
import com.moderation.ipblock.service.IpBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "IpBlock", description = "[ADMIN] IP 차단 API")
@RestController
@RequestMapping("/api/moderation/ip-blocks")
@RequiredArgsConstructor
public class IpBlockController {

    private final IpBlockService ipBlockService;

    @Operation(summary = "[ADMIN] IP 차단 등록")
    @PostMapping
    public ResponseEntity<IpBlockResponse> blockIp(
            @RequestHeader("X-Member-Id") Long adminMemberId,
            @RequestBody CreateIpBlockRequest request
    ) {
        return ResponseEntity.ok(ipBlockService.blockIp(adminMemberId, request));
    }

    @Operation(summary = "[ADMIN] IP 차단 목록 조회")
    @GetMapping
    public ResponseEntity<Page<IpBlockResponse>> getIpBlockList(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ipBlockService.getIpBlockList(pageable));
    }

    @Operation(summary = "[ADMIN] IP 차단 만료일 변경")
    @PatchMapping("/{id}")
    public ResponseEntity<IpBlockResponse> updateIpBlock(
            @RequestHeader("X-Member-Id") Long adminMemberId,
            @PathVariable Long id,
            @RequestBody UpdateIpBlockRequest request
    ) {
        return ResponseEntity.ok(ipBlockService.updateIpBlock(adminMemberId, id, request));
    }

    @Operation(summary = "[ADMIN] IP 차단 해제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unblockIp(
            @RequestHeader("X-Member-Id") Long adminMemberId,
            @PathVariable Long id
    ) {
        ipBlockService.unblockIp(adminMemberId, id);
        return ResponseEntity.noContent().build();
    }
}
