package com.supermarket.modules.losses.api;

import com.supermarket.modules.losses.application.LossRecordService;
import com.supermarket.modules.losses.domain.LossRecord;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.PageResponse;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/losses")
@RequiredArgsConstructor
public class LossRecordController {

    private final LossRecordService lossRecordService;

    @GetMapping
    public ApiResponse<PageResponse<LossRecord>> list(@PageableDefault(size = 20) Pageable pageable) {
        UUID branchId = BranchContext.getBranchId()
                .orElseThrow(() -> new IllegalArgumentException("X-Branch-Id header required"));
        return ApiResponse.success(PageResponse.from(lossRecordService.listByBranch(branchId, pageable)));
    }

    @PostMapping
    public ApiResponse<LossRecord> create(@Valid @RequestBody LossRecord record) {
        return ApiResponse.success(lossRecordService.create(record));
    }

    @GetMapping("/{id}")
    public ApiResponse<LossRecord> get(@PathVariable UUID id) {
        return ApiResponse.success(lossRecordService.getById(id));
    }
}
