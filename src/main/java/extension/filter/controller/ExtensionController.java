package extension.filter.controller;

import extension.filter.dto.request.CustomExtensionRequest;
import extension.filter.dto.request.FixedExtensionUpdateRequest;
import extension.filter.dto.response.CustomExtensionResponse;
import extension.filter.dto.response.FixedExtensionResponse;
import extension.filter.service.ExtensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionService extensionService;

    @GetMapping("/fixed")
    public List<FixedExtensionResponse> getAllFixedExtensions() {
        return extensionService.getAllFixedExtensions();
    }

    @PatchMapping("/fixed/{extension}")
    public FixedExtensionResponse updateFixedExtension(
            @PathVariable String extension,
            @Valid @RequestBody FixedExtensionUpdateRequest request) {
        return extensionService.updateFixedExtension(extension, request.checked());
    }

    @GetMapping("/custom")
    public List<CustomExtensionResponse> getAllCustomExtensions() {
        return extensionService.getAllCustomExtensions();
    }

    @PostMapping("/custom")
    public CustomExtensionResponse addCustomExtension(
            @Valid @RequestBody CustomExtensionRequest request) {
        return extensionService.addCustomExtension(request);
    }

    @DeleteMapping("/custom/{id}")
    public void deleteCustomExtension(@PathVariable Long id) {
        extensionService.deleteCustomExtension(id);
    }
}

