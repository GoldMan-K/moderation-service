package com.moderation.report.repository;

import com.moderation.report.domain.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 동일 대상 중복 신고 여부 확인
    boolean existsByTargetTypeAndTargetIdAndReporterMemberId(
            String targetType, Long targetId, Long reporterMemberId);

    // 상태·타입 필터 목록
    @Query("""
            SELECT r FROM Report r
            WHERE (:status     IS NULL OR r.status     = :status)
              AND (:targetType IS NULL OR r.targetType = :targetType)
            ORDER BY r.createdAt DESC
            """)
    Page<Report> findAllByFilter(
            @Param("status") String status,
            @Param("targetType") String targetType,
            Pageable pageable
    );
}
