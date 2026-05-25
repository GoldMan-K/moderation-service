package com.moderation.keyword.repository;

import com.moderation.keyword.domain.ProfanityKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfanityKeywordRepository extends JpaRepository<ProfanityKeyword, Long> {

    boolean existsByKeyword(String keyword);

    List<ProfanityKeyword> findAllByUseYn(String useYn);
}
