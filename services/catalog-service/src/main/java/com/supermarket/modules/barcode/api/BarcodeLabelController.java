package com.supermarket.modules.barcode.api;

import com.supermarket.modules.barcode.application.BarcodeLabelService;
import com.supermarket.modules.barcode.domain.LabelTemplate;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/barcode")
@RequiredArgsConstructor
public class BarcodeLabelController {

    private final BarcodeLabelService barcodeLabelService;

    @GetMapping("/templates")
    public ApiResponse<List<LabelTemplate>> listTemplates() {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        return ApiResponse.success(barcodeLabelService.listTemplates(orgId));
    }

    @PostMapping("/templates")
    public ApiResponse<LabelTemplate> createTemplate(@Valid @RequestBody LabelTemplate template) {
        return ApiResponse.success(barcodeLabelService.createTemplate(template));
    }

    @GetMapping("/labels/print")
    public ApiResponse<Map<String, Object>> printLabel(
            @RequestParam UUID productId,
            @RequestParam UUID templateId) {
        return ApiResponse.success(barcodeLabelService.generateProductLabel(productId, templateId));
    }
}
