package extension.filter.dto.response;

import extension.filter.entity.CustomExtension;

public record CustomExtensionResponse(
        Long id,
        String extension
) {
    public static CustomExtensionResponse from(CustomExtension entity) {
        return new CustomExtensionResponse(
                entity.getId(),
                entity.getExtension()
        );
    }
}
