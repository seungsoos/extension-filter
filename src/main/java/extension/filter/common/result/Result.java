package extension.filter.common.result;

import lombok.Getter;

@Getter
public enum Result {
    SUCCESS("SUCCESS", "성공"),
    BAD_REQUEST("BAD_REQUEST", "잘못된 요청입니다"),
    NOT_FOUND("NOT_FOUND", "존재하지 않는 정보입니다"),
    DUPLICATE("DUPLICATE", "이미 존재하는 정보입니다"),
    LIMIT_EXCEEDED("LIMIT_EXCEEDED", "최대 개수를 초과했습니다"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류입니다");

    private final String code;
    private final String message;

    Result(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
