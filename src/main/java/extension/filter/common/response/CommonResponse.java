package extension.filter.common.response;

import extension.filter.common.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Meta meta;
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(new Meta(Result.SUCCESS), data);
    }

    public static <T> CommonResponse<T> error(Result result, String message) {
        return new CommonResponse<>(new Meta(result, message), null);
    }

    public static <T> CommonResponse<ListResponse<T>> listOf(List<T> items) {
        return new CommonResponse<>(new Meta(Result.SUCCESS), new ListResponse<>(items, items.size()));
    }

    @Getter
    @NoArgsConstructor
    public static class Meta {
        private String code;
        private String message;

        public Meta(Result result) {
            this.code = result.getCode();
            this.message = result.getMessage();
        }

        public Meta(Result result, String message) {
            this.code = result.getCode();
            this.message = message;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListResponse<T> {
        private List<T> items;
        private int total;
    }
}
