package com.supermarket.modules.purchases.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class GoodsReceiptResponse {

    private UUID id;
    private UUID orderId;
    private String receiptNumber;
    private String status;
    private LocalDate receiptDate;
    private String notes;
    private List<GoodsReceiptLineResponse> lines;
}
