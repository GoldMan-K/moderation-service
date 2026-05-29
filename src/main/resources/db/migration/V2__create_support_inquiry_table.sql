CREATE TABLE IF NOT EXISTS support_inquiry (
    id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '문의 PK',
    member_id             BIGINT UNSIGNED NOT NULL COMMENT '문의 작성자 회원 ID (Member Service ID 값 참조, FK 미사용)',
    title                 VARCHAR(120)    NOT NULL COMMENT '문의 제목',
    content               TEXT            NOT NULL COMMENT '문의 내용 본문',

    status                VARCHAR(20)     NOT NULL DEFAULT 'RECEIVED' COMMENT '문의 상태: RECEIVED, ANSWERED, CLOSED',
    admin_reply           TEXT            NULL COMMENT '관리자 답변 내용',
    answered_by_member_id BIGINT UNSIGNED NULL COMMENT '답변 처리한 관리자 회원 ID',
    answered_at           DATETIME(3)     NULL COMMENT '답변 처리 시각',

    created_at            DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '생성 시각',
    updated_at            DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                          ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '상태/답변 변경 시각',
    deleted_at            DATETIME(3)     NULL COMMENT '소프트 삭제 시각',
    deleted_by_member_id  BIGINT UNSIGNED NULL COMMENT '소프트 삭제 처리 회원 ID',

    PRIMARY KEY (id),

    KEY idx_support_inquiry_member_created (member_id, created_at),
    KEY idx_support_inquiry_status_created (status, created_at),

    CONSTRAINT ck_support_inquiry_status
      CHECK (status IN ('RECEIVED', 'ANSWERED', 'CLOSED')),

    CONSTRAINT ck_support_inquiry_answer_fields
      CHECK (
        (status = 'RECEIVED' AND answered_at IS NULL)
        OR
        (status IN ('ANSWERED', 'CLOSED') AND admin_reply IS NOT NULL AND answered_at IS NOT NULL)
      )
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='사용자 문의(고객센터) 데이터';

