package com.moderation.supportinquiry.repository;

import com.moderation.supportinquiry.domain.SupportInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SupportInquiryRepository extends JpaRepository<SupportInquiry, Long> {

    Page<SupportInquiry> findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Optional<SupportInquiry> findByIdAndMemberIdAndDeletedAtIsNull(Long id, Long memberId);

    Optional<SupportInquiry> findByIdAndDeletedAtIsNull(Long id);

    @Query("""
            SELECT s FROM SupportInquiry s
            WHERE s.deletedAt IS NULL
              AND (:status IS NULL OR s.status = :status)
            ORDER BY s.createdAt DESC
            """)
    Page<SupportInquiry> findAllByStatusFilter(@Param("status") String status, Pageable pageable);
}

