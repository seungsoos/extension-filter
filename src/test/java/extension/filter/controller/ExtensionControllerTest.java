package extension.filter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import extension.filter.dto.request.CustomExtensionRequest;
import extension.filter.dto.request.FixedExtensionUpdateRequest;
import extension.filter.dto.response.CustomExtensionResponse;
import extension.filter.dto.response.FixedExtensionResponse;
import extension.filter.service.ExtensionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExtensionController.class)
class ExtensionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExtensionService extensionService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("모든 고정 확장자를 조회한다")
    void getAllFixedExtensions() throws Exception {
        List<FixedExtensionResponse> responses = Arrays.asList(
                new FixedExtensionResponse(1L, "bat", false),
                new FixedExtensionResponse(2L, "cmd", true),
                new FixedExtensionResponse(3L, "com", false)
        );

        when(extensionService.getAllFixedExtensions()).thenReturn(responses);

        mockMvc.perform(get("/api/extensions/fixed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.items[0].extension").value("bat"))
                .andExpect(jsonPath("$.data.items[0].checked").value(false))
                .andExpect(jsonPath("$.data.items[1].id").value(2))
                .andExpect(jsonPath("$.data.items[1].extension").value("cmd"))
                .andExpect(jsonPath("$.data.items[1].checked").value(true))
                .andExpect(jsonPath("$.data.total").value(3));
    }

    @Test
    @DisplayName("고정 확장자의 체크 상태를 업데이트한다")
    void updateFixedExtension() throws Exception {
        FixedExtensionUpdateRequest request = new FixedExtensionUpdateRequest(true);
        FixedExtensionResponse response = new FixedExtensionResponse(1L, "bat", true);

        when(extensionService.updateFixedExtension(eq("bat"), eq(true))).thenReturn(response);

        mockMvc.perform(patch("/api/extensions/fixed/bat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.extension").value("bat"))
                .andExpect(jsonPath("$.data.checked").value(true));
    }

    @Test
    @DisplayName("고정 확장자 업데이트 시 체크 값이 null이면 validation 실패한다")
    void updateFixedExtension_withNullChecked_shouldFail() throws Exception {
        String invalidRequest = "{\"checked\": null}";

        mockMvc.perform(patch("/api/extensions/fixed/bat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("모든 커스텀 확장자를 조회한다")
    void getAllCustomExtensions() throws Exception {
        List<CustomExtensionResponse> responses = Arrays.asList(
                new CustomExtensionResponse(1L, "cpp"),
                new CustomExtensionResponse(2L, "java"),
                new CustomExtensionResponse(3L, "py")
        );

        when(extensionService.getAllCustomExtensions()).thenReturn(responses);

        mockMvc.perform(get("/api/extensions/custom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.items[0].extension").value("cpp"))
                .andExpect(jsonPath("$.data.items[1].id").value(2))
                .andExpect(jsonPath("$.data.items[1].extension").value("java"))
                .andExpect(jsonPath("$.data.items[2].id").value(3))
                .andExpect(jsonPath("$.data.items[2].extension").value("py"))
                .andExpect(jsonPath("$.data.total").value(3));
    }

    @Test
    @DisplayName("커스텀 확장자를 추가한다")
    void addCustomExtension() throws Exception {
        CustomExtensionRequest request = new CustomExtensionRequest("cpp");
        CustomExtensionResponse response = new CustomExtensionResponse(1L, "cpp");

        when(extensionService.addCustomExtension(any(CustomExtensionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/extensions/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.extension").value("cpp"));
    }

    @Test
    @DisplayName("커스텀 확장자 추가 시 확장자가 빈 값이면 validation 실패한다")
    void addCustomExtension_withBlankExtension_shouldFail() throws Exception {
        CustomExtensionRequest request = new CustomExtensionRequest("");

        mockMvc.perform(post("/api/extensions/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("커스텀 확장자 추가 시 확장자가 20자를 초과하면 validation 실패한다")
    void addCustomExtension_withTooLongExtension_shouldFail() throws Exception {
        CustomExtensionRequest request = new CustomExtensionRequest("a".repeat(21));

        mockMvc.perform(post("/api/extensions/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("커스텀 확장자 추가 시 확장자에 특수문자가 포함되면 validation 실패한다")
    void addCustomExtension_withSpecialCharacters_shouldFail() throws Exception {
        CustomExtensionRequest request = new CustomExtensionRequest("c++");

        mockMvc.perform(post("/api/extensions/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("커스텀 확장자를 삭제한다")
    void deleteCustomExtension() throws Exception {
        doNothing().when(extensionService).deleteCustomExtension(anyLong());

        mockMvc.perform(delete("/api/extensions/custom/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("SUCCESS"));
    }
}
