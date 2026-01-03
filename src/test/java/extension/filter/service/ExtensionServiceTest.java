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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtensionService 테스트")
class ExtensionServiceTest {

    @Mock
    private FixedExtensionRepository fixedExtensionRepository;

    @Mock
    private CustomExtensionRepository customExtensionRepository;

    @Mock
    private RedisConfigService redisConfigService;

    @InjectMocks
    private ExtensionService extensionService;

    @Test
    @DisplayName("고정 확장자 전체 조회 성공")
    void getAllFixedExtensions_Success() {
        // given
        List<FixedExtension> entities = List.of(
                FixedExtension.create("bat"),
                FixedExtension.create("cmd"),
                FixedExtension.create("exe")
        );
        given(fixedExtensionRepository.findAll()).willReturn(entities);

        // when
        List<FixedExtensionResponse> result = extensionService.getAllFixedExtensions();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).extension()).isEqualTo("bat");
        assertThat(result.get(1).extension()).isEqualTo("cmd");
        assertThat(result.get(2).extension()).isEqualTo("exe");
        verify(fixedExtensionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("고정 확장자 체크 상태 수정 성공")
    void updateFixedExtension_Success() {
        // given
        String extension = "bat";
        FixedExtension entity = FixedExtension.create(extension);
        given(fixedExtensionRepository.findByExtension(extension)).willReturn(Optional.of(entity));

        // when
        FixedExtensionResponse result = extensionService.updateFixedExtension(extension, true);

        // then
        assertThat(result.extension()).isEqualTo("bat");
        assertThat(result.checked()).isTrue();
        verify(fixedExtensionRepository, times(1)).findByExtension(extension);
    }

    @Test
    @DisplayName("고정 확장자 체크 상태 수정 실패 - 존재하지 않는 확장자")
    void updateFixedExtension_Fail_NotFound() {
        // given
        String extension = "unknown";
        given(fixedExtensionRepository.findByExtension(extension)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> extensionService.updateFixedExtension(extension, true))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("존재하지 않는 고정 확장자입니다");
    }

    @Test
    @DisplayName("커스텀 확장자 전체 조회 성공")
    void getAllCustomExtensions_Success() {
        // given
        List<CustomExtension> entities = List.of(
                CustomExtension.create("pdf"),
                CustomExtension.create("hwp")
        );
        given(customExtensionRepository.findAll()).willReturn(entities);

        // when
        List<CustomExtensionResponse> result = extensionService.getAllCustomExtensions();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).extension()).isEqualTo("pdf");
        assertThat(result.get(1).extension()).isEqualTo("hwp");
        verify(customExtensionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("커스텀 확장자 추가 성공")
    void addCustomExtension_Success() {
        // given
        CustomExtensionRequest request = new CustomExtensionRequest("PDF");
        CustomExtension entity = CustomExtension.create("pdf");

        given(customExtensionRepository.existsByExtension("pdf")).willReturn(false);
        given(redisConfigService.getMaxCustomExtensions()).willReturn(200);
        given(customExtensionRepository.countBy()).willReturn(0L);
        given(customExtensionRepository.save(any(CustomExtension.class))).willReturn(entity);

        // when
        CustomExtensionResponse result = extensionService.addCustomExtension(request);

        // then
        assertThat(result.extension()).isEqualTo("pdf");
        verify(customExtensionRepository, times(1)).existsByExtension("pdf");
        verify(customExtensionRepository, times(1)).save(any(CustomExtension.class));
    }

    @Test
    @DisplayName("커스텀 확장자 추가 실패 - 중복")
    void addCustomExtension_Fail_Duplicate() {
        // given
        CustomExtensionRequest request = new CustomExtensionRequest("pdf");
        given(customExtensionRepository.existsByExtension("pdf")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> extensionService.addCustomExtension(request))
                .isInstanceOf(BizException.class)
                .extracting("result")
                .isEqualTo(Result.DUPLICATE);
    }

    @Test
    @DisplayName("커스텀 확장자 추가 실패 - 최대 개수 초과")
    void addCustomExtension_Fail_LimitExceeded() {
        // given
        CustomExtensionRequest request = new CustomExtensionRequest("pdf");
        given(customExtensionRepository.existsByExtension("pdf")).willReturn(false);
        given(redisConfigService.getMaxCustomExtensions()).willReturn(200);
        given(customExtensionRepository.countBy()).willReturn(200L);

        // when & then
        assertThatThrownBy(() -> extensionService.addCustomExtension(request))
                .isInstanceOf(BizException.class)
                .extracting("result")
                .isEqualTo(Result.LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("커스텀 확장자 추가 - 대소문자 및 공백 처리")
    void addCustomExtension_NormalizeExtension() {
        // given
        CustomExtensionRequest request = new CustomExtensionRequest("  PDF  ");
        CustomExtension entity = CustomExtension.create("pdf");

        given(customExtensionRepository.existsByExtension("pdf")).willReturn(false);
        given(redisConfigService.getMaxCustomExtensions()).willReturn(200);
        given(customExtensionRepository.countBy()).willReturn(0L);
        given(customExtensionRepository.save(any(CustomExtension.class))).willReturn(entity);

        // when
        CustomExtensionResponse result = extensionService.addCustomExtension(request);

        // then
        assertThat(result.extension()).isEqualTo("pdf");
        verify(customExtensionRepository).existsByExtension("pdf");
    }

    @Test
    @DisplayName("커스텀 확장자 삭제 성공")
    void deleteCustomExtension_Success() {
        // given
        Long id = 1L;
        given(customExtensionRepository.existsById(id)).willReturn(true);
        doNothing().when(customExtensionRepository).deleteById(id);

        // when
        extensionService.deleteCustomExtension(id);

        // then
        verify(customExtensionRepository, times(1)).existsById(id);
        verify(customExtensionRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("커스텀 확장자 삭제 실패 - 존재하지 않는 ID")
    void deleteCustomExtension_Fail_NotFound() {
        // given
        Long id = 999L;
        given(customExtensionRepository.existsById(id)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> extensionService.deleteCustomExtension(id))
                .isInstanceOf(BizException.class)
                .extracting("result")
                .isEqualTo(Result.NOT_FOUND);
    }
}
