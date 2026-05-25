package com.moderation.keyword.service;

import com.moderation.global.exception.BusinessException;
import com.moderation.global.exception.ErrorCode;
import com.moderation.keyword.domain.ProfanityKeyword;
import com.moderation.keyword.repository.ProfanityKeywordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfanityKeywordService {

    private final ProfanityKeywordRepository keywordRepository;

    // 애플리케이션 캐시 — config.taxonomy.changed 이벤트 수신 시 갱신
    private final CopyOnWriteArrayList<String> cachedKeywords = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void initCache() {
        refreshCache();
    }

    public void refreshCache() {
        List<String> keywords = keywordRepository.findAllByUseYn("Y")
                .stream()
                .map(ProfanityKeyword::getKeyword)
                .toList();
        cachedKeywords.clear();
        cachedKeywords.addAll(keywords);
        log.info("[ProfanityFilter] 캐시 갱신 완료: {}개 키워드", cachedKeywords.size());
    }

    /**
     * 게시글 내용에 욕설·스팸 포함 여부 확인
     */
    public void checkAndFilter(Long postId, String content) {
        for (String keyword : cachedKeywords) {
            if (content.contains(keyword)) {
                log.warn("[ProfanityFilter] 욕설 감지 — postId={}, keyword={}", postId, keyword);
                // TODO: 감지 시 Community Service로 게시글 HIDDEN 처리 이벤트 발행
                break;
            }
        }
    }

    public List<ProfanityKeyword> getAllKeywords() {
        return keywordRepository.findAll();
    }

    @Transactional
    public ProfanityKeyword addKeyword(String keyword, String type) {
        if (keywordRepository.existsByKeyword(keyword)) {
            throw new BusinessException(ErrorCode.KEYWORD_ALREADY_EXISTS);
        }
        ProfanityKeyword entity = ProfanityKeyword.builder()
                .keyword(keyword)
                .type(type)
                .build();
        ProfanityKeyword saved = keywordRepository.save(entity);
        cachedKeywords.add(keyword);
        return saved;
    }

    @Transactional
    public void deactivateKeyword(Long id) {
        ProfanityKeyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.KEYWORD_NOT_FOUND));
        keyword.deactivate();
        cachedKeywords.remove(keyword.getKeyword());
    }
}
