CREATE TABLE IF NOT EXISTS report (
    id                        BIGINT        NOT NULL AUTO_INCREMENT       COMMENT '신고 PK',
    target_type               VARCHAR(20)   NOT NULL                      COMMENT 'POST|COMMENT|MEETUP_CHAT',
    target_id                 BIGINT        NOT NULL                      COMMENT '신고 대상 ID',
    reported_writer_member_id BIGINT        NULL                          COMMENT '피신고자 member_id',
    reporter_member_id        BIGINT        NULL                          COMMENT '신고자 member_id (NULL이면 비회원)',
    reporter_ip               VARCHAR(45)   NULL                          COMMENT '신고자 IP',
    reason_code               VARCHAR(50)   NOT NULL                      COMMENT '신고 사유 코드',
    detail                    TEXT          NULL                          COMMENT '상세 내용',
    status                    VARCHAR(10)   NOT NULL DEFAULT 'PENDING'    COMMENT 'PENDING|RESOLVED|REJECTED',
    handled_by_member_id      BIGINT        NULL                          COMMENT '처리자 member_id',
    handled_at                DATETIME(3)   NULL,
    memo                      TEXT          NULL                          COMMENT '관리자 처리 메모',
    created_at                DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_report_target   (target_type, target_id),
    KEY idx_report_reporter (reporter_member_id),
    KEY idx_report_status   (status),
    KEY idx_report_reason   (reason_code),
    KEY idx_report_created  (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='신고';

CREATE TABLE IF NOT EXISTS ip_block (
    id                    BIGINT        NOT NULL AUTO_INCREMENT   COMMENT 'IP 차단 PK',
    ip                    VARCHAR(45)   NOT NULL                  COMMENT '차단 IP (IPv6 대응)',
    reason                VARCHAR(255)  NOT NULL                  COMMENT '차단 사유',
    blocked_by_member_id  BIGINT        NOT NULL                  COMMENT '처리자 member_id',
    blocked_at            DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    expires_at            DATETIME(3)   NULL                      COMMENT 'NULL이면 영구 차단',
    PRIMARY KEY (id),
    UNIQUE KEY uq_ip        (ip),
    KEY idx_ip_blocked_at  (blocked_at),
    KEY idx_ip_expires_at  (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IP 차단';

CREATE TABLE IF NOT EXISTS moderation_action_log (
    id           BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '액션 로그 PK',
    actor_member_id BIGINT     NOT NULL                 COMMENT '액션 수행자 member_id',
    action_type  VARCHAR(30)   NOT NULL                 COMMENT '액션 유형',
    target_type  VARCHAR(20)   NOT NULL                 COMMENT '대상 타입',
    target_id    BIGINT        NOT NULL                 COMMENT '대상 ID',
    reason       VARCHAR(255)  NULL                     COMMENT '처리 사유',
    meta_json    JSON          NULL                     COMMENT '이전값/이후값 등 추가 데이터',
    created_at   DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_action_actor   (actor_member_id),
    KEY idx_action_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='관리자 액션 로그 (INSERT only)';

CREATE TABLE IF NOT EXISTS profanity_keyword (
    id         BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '욕설 키워드 PK',
    keyword    VARCHAR(100)  NOT NULL                 COMMENT '키워드 또는 정규식 패턴',
    type       VARCHAR(10)   NOT NULL DEFAULT 'PROFANITY' COMMENT 'PROFANITY|SPAM',
    use_yn     CHAR(1)       NOT NULL DEFAULT 'Y',
    created_at DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uq_keyword (keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='욕설/스팸 키워드';
