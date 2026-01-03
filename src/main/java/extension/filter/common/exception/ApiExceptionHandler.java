package extension.filter.common.exception;

import extension.filter.common.response.CommonResponse;
import extension.filter.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public CommonResponse<Void> exception(Exception ex) {
        log.error("Exception caught: {}", ex.getMessage(), ex);
        return CommonResponse.error(Result.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = BizException.class)
    public CommonResponse<Void> bizException(BizException ex) {
        log.error("BizException caught: {}", ex.getMessage(), ex);
        return CommonResponse.error(ex.getResult(), ex.getErrorMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResponse<Void> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException caught: {}", ex.getMessage(), ex);

        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return CommonResponse.error(Result.BAD_REQUEST, errors.toString());
    }
}
