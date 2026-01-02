package extension.filter.common.exception;

import extension.filter.common.result.Result;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final Result result;
    private final String errorMessage;

    public BizException(Result result, String errorMessage) {
        super(errorMessage);
        this.result = result;
        this.errorMessage = errorMessage != null ? errorMessage : result.getMessage();
    }

    public BizException(Result result) {
        this(result, result.getMessage());
    }
}
