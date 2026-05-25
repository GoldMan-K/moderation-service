package com.moderation.actionlog.repository;

import com.moderation.actionlog.domain.ModerationActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationActionLogRepository extends JpaRepository<ModerationActionLog, Long> {
}
