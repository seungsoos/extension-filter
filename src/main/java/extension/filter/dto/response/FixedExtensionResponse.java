package extension.filter.dto.response;

import extension.filter.entity.FixedExtension;

public record FixedExtensionResponse(
        Long id,
        String extension,
        Boolean checked
) {
    public static FixedExtensionResponse from(FixedExtension entity) {
        return new FixedExtensionResponse(
                entity.getId(),
                entity.getExtension(),
                entity.getIsChecked()
        );
    }
}
