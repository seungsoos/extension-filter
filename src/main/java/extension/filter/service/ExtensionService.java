package extension.filter.service;

import extension.filter.common.exception.BizException;
import extension.filter.common.result.Result;
import extension.filter.dto.request.CustomExtensionRequest;
import extension.filter.dto.response.CustomExtensionResponse;
import extension.filter.dto.response.FixedExtensionResponse;
import extension.filter.entity.CustomExtension;
import extension.filter.entity.FixedExtension;
import extension.filter.repository.CustomExtensionRepository;
import extension.filter.repository.FixedExtensionRepository;
import extension.filter.service.config.RedisConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtensionService {

    private final FixedExtensionRepository fixedExtensionRepository;
    private final CustomExtensionRepository customExtensionRepository;
    private final RedisConfigService redisConfigService;

    @Transactional(readOnly = true)
    public List<FixedExtensionResponse> getAllFixedExtensions() {
        return fixedExtensionRepository.findAll().stream()
                .map(FixedExtensionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public FixedExtensionResponse updateFixedExtension(String extension, Boolean checked) {
        FixedExtension fixedExtension = fixedExtensionRepository.findByExtension(extension)
                .orElseThrow(() -> new BizException(Result.NOT_FOUND, "존재하지 않는 고정 확장자입니다: " + extension));

        fixedExtension.updateChecked(checked);

        return FixedExtensionResponse.from(fixedExtension);
    }

    @Transactional(readOnly = true)
    public List<CustomExtensionResponse> getAllCustomExtensions() {
        return customExtensionRepository.findAll().stream()
                .map(CustomExtensionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomExtensionResponse addCustomExtension(CustomExtensionRequest request) {
        String extension = request.extension().trim().toLowerCase();

        if (customExtensionRepository.existsByExtension(extension)) {
            throw new BizException(Result.DUPLICATE, "이미 존재하는 확장자입니다: " + extension);
        }

        Integer maxCustomExtensions = redisConfigService.getMaxCustomExtensions();
        long count = customExtensionRepository.countBy();
        if (count >= maxCustomExtensions) {
            throw new BizException(Result.LIMIT_EXCEEDED, "커스텀 확장자는 최대 " + maxCustomExtensions + "개까지 추가 가능합니다");
        }

        CustomExtension customExtension = CustomExtension.create(extension);
        CustomExtension saved = customExtensionRepository.save(customExtension);

        return CustomExtensionResponse.from(saved);
    }

    @Transactional
    public void deleteCustomExtension(Long id) {
        if (!customExtensionRepository.existsById(id)) {
            throw new BizException(Result.NOT_FOUND, "존재하지 않는 커스텀 확장자입니다");
        }
        customExtensionRepository.deleteById(id);
    }
}
