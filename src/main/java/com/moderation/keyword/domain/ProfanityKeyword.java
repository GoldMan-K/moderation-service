package com.moderation.keyword.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "profanity_keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfanityKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;
    private String type;   // PROFANITY | SPAM
    private String useYn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ProfanityKeyword(String keyword, String type) {
        this.keyword = keyword;
        this.type = type;
        this.useYn = "Y";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.useYn = "N";
    }

    public void activate() {
        this.useYn = "Y";
    }

    public boolean isActive() {
        return "Y".equals(this.useYn);
    }
}
