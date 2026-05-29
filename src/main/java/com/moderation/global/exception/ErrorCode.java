package com.moderation.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),

    // 신고
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고 내역을 찾을 수 없습니다."),
    REPORT_ALREADY_HANDLED(HttpStatus.BAD_REQUEST, "이미 처리된 신고입니다."),
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "이미 신고한 대상입니다."),

    // IP 차단
    IP_BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "IP 차단 정보를 찾을 수 없습니다."),
    IP_ALREADY_BLOCKED(HttpStatus.CONFLICT, "이미 차단된 IP입니다."),

    // 키워드
    KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "키워드를 찾을 수 없습니다."),
    KEYWORD_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 키워드입니다."),

    // 문의
    SUPPORT_INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "문의 내역을 찾을 수 없습니다."),
    SUPPORT_INQUIRY_ALREADY_HANDLED(HttpStatus.BAD_REQUEST, "이미 처리된 문의입니다."),
    SUPPORT_INQUIRY_INVALID_STATUS(HttpStatus.BAD_REQUEST, "변경 가능한 문의 상태가 아닙니다."),
    SUPPORT_INQUIRY_REPLY_REQUIRED(HttpStatus.BAD_REQUEST, "답변 내용은 필수입니다.");

    private final HttpStatus status;
    private final String message;
}
