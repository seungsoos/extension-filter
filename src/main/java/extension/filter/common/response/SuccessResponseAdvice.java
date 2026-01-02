package extension.filter.common.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
public class SuccessResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        // 이미 CommonResponse인 경우 그대로 반환
        if (body instanceof CommonResponse) {
            return body;
        }

        // byte[] 같은 바이너리 데이터는 그대로 반환
        if (body instanceof byte[]) {
            return body;
        }

        // void 반환 메서드(body가 null)인 경우 빈 성공 응답
        if (body == null) {
            return CommonResponse.success(null);
        }

        // List인 경우 CommonResponse.listOf로 래핑
        if (body instanceof List<?> listBody) {
            return CommonResponse.listOf(listBody);
        }

        // 나머지는 CommonResponse.success로 래핑
        return CommonResponse.success(body);
    }
}
