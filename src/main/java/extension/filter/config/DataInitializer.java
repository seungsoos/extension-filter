package extension.filter.config;

import extension.filter.entity.FixedExtension;
import extension.filter.repository.FixedExtensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FixedExtensionRepository fixedExtensionRepository;

    private static final List<String> FIXED_EXTENSIONS = List.of(
            "bat", "cmd", "com", "cpl", "exe", "scr", "js"
    );

    @Override
    public void run(String... args) {
        initFixedExtensions();
    }

    private void initFixedExtensions() {
        long existingCount = fixedExtensionRepository.count();

        if (existingCount > 0) {
            log.info("Fixed extensions 저장 완료");
            return;
        }

        List<FixedExtension> fixedExtensions = FIXED_EXTENSIONS.stream()
                .map(FixedExtension::create)
                .toList();

        fixedExtensionRepository.saveAll(fixedExtensions);
    }
}