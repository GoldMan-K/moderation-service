package com.moderation.ipblock.service;

import com.moderation.actionlog.domain.ModerationActionLog;
import com.moderation.actionlog.repository.ModerationActionLogRepository;
import com.moderation.global.exception.BusinessException;
import com.moderation.global.exception.ErrorCode;
import com.moderation.ipblock.domain.IpBlock;
import com.moderation.ipblock.dto.CreateIpBlockRequest;
import com.moderation.ipblock.dto.IpBlockResponse;
import com.moderation.ipblock.dto.UpdateIpBlockRequest;
import com.moderation.ipblock.repository.IpBlockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IpBlockService {

    private final IpBlockRepository ipBlockRepository;
    private final ModerationActionLogRepository actionLogRepository;

    // ─── IP 차단 등록 [ADMIN] ────────────────────────────────────────────────

    @Transactional
    public IpBlockResponse blockIp(Long adminMemberId, CreateIpBlockRequest request) {
        if (ipBlockRepository.existsByIp(request.ip())) {
            throw new BusinessException(ErrorCode.IP_ALREADY_BLOCKED);
        }

        IpBlock ipBlock = IpBlock.builder()
                .ip(request.ip())
                .reason(request.reason())
                .blockedByMemberId(adminMemberId)
                .expiresAt(request.expiresAt())
                .build();
        ipBlockRepository.save(ipBlock);

        actionLogRepository.save(ModerationActionLog.builder()
                .actorMemberId(adminMemberId)
                .actionType("IP_BLOCK")
                .targetType("IP")
                .targetId(ipBlock.getId())
                .reason(request.reason())
                .build());

        log.info("[IpBlock] IP 차단 등록: ip={}, adminId={}", request.ip(), adminMemberId);
        return IpBlockResponse.from(ipBlock);
    }

    // ─── IP 차단 목록 [ADMIN] ────────────────────────────────────────────────

    public Page<IpBlockResponse> getIpBlockList(Pageable pageable) {
        return ipBlockRepository.findAll(pageable).map(IpBlockResponse::from);
    }

    // ─── 만료일 변경 [ADMIN] ─────────────────────────────────────────────────

    @Transactional
    public IpBlockResponse updateIpBlock(Long adminMemberId, Long id, UpdateIpBlockRequest request) {
        IpBlock ipBlock = ipBlockRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.IP_BLOCK_NOT_FOUND));
        ipBlock.updateExpiry(request.expiresAt());

        actionLogRepository.save(ModerationActionLog.builder()
                .actorMemberId(adminMemberId)
                .actionType("IP_BLOCK_UPDATE")
                .targetType("IP")
                .targetId(id)
                .reason("만료일 변경: " + request.expiresAt())
                .build());

        return IpBlockResponse.from(ipBlock);
    }

    // ─── IP 차단 해제 [ADMIN] ────────────────────────────────────────────────

    @Transactional
    public void unblockIp(Long adminMemberId, Long id) {
        IpBlock ipBlock = ipBlockRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.IP_BLOCK_NOT_FOUND));

        actionLogRepository.save(ModerationActionLog.builder()
                .actorMemberId(adminMemberId)
                .actionType("IP_UNBLOCK")
                .targetType("IP")
                .targetId(id)
                .reason("IP 차단 해제: " + ipBlock.getIp())
                .build());

        ipBlockRepository.delete(ipBlock);
        log.info("[IpBlock] IP 차단 해제: ip={}, adminId={}", ipBlock.getIp(), adminMemberId);
    }
}
