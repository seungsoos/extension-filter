package extension.filter.service.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisConfigService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CONFIG_KEY_PREFIX = "config:";
    private static final String MAX_CUSTOM_EXTENSIONS_KEY = CONFIG_KEY_PREFIX + "max_custom_extensions";

    @PostConstruct
    public void init() {
        Object value = redisTemplate.opsForValue().get(MAX_CUSTOM_EXTENSIONS_KEY);
        if (value == null) {
            redisTemplate.opsForValue().set(MAX_CUSTOM_EXTENSIONS_KEY, 200);
        } else {
            log.info("custom extensions 수정 = {}", value);
        }
    }

    public Integer getMaxCustomExtensions() {
        Object value = redisTemplate.opsForValue().get(MAX_CUSTOM_EXTENSIONS_KEY);
        if (value == null) {
            log.warn("MAX_CUSTOM_EXTENSIONS Reids 조회되지 않음.");
            return 200;
        }
        return Integer.parseInt(value.toString());
    }
}
