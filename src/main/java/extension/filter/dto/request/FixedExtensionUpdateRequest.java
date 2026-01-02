package extension.filter.dto.request;

import jakarta.validation.constraints.NotNull;

public record FixedExtensionUpdateRequest(
        @NotNull(message = "체크 여부는 필수입니다")
        Boolean checked
) {
}
