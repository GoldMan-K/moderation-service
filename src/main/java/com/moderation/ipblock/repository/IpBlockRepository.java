package com.moderation.ipblock.repository;

import com.moderation.ipblock.domain.IpBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpBlockRepository extends JpaRepository<IpBlock, Long> {

    boolean existsByIp(String ip);

    Optional<IpBlock> findByIp(String ip);
}
