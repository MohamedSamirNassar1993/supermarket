package com.supermarket.modules.inventory.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.common.events.DomainEvent;
import com.supermarket.common.events.StockReceivedEvent;
import com.supermarket.modules.inventory.application.dto.ReceiveStockRequest;
import com.supermarket.modules.inventory.application.InventoryService;
import com.supermarket.shared.infrastructure.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockReceivedEventListener {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.PLATFORM_EVENTS_QUEUE)
    public void onPlatformEvent(String payload) {
        try {
            DomainEvent<?> event = objectMapper.readValue(payload, DomainEvent.class);
            if (!StockReceivedEvent.TYPE.equals(event.eventType())) {
                return;
            }
            StockReceivedEvent stockEvent = objectMapper.convertValue(event.payload(), StockReceivedEvent.class);
            inventoryService.receiveStock(ReceiveStockRequest.builder()
                    .organizationId(stockEvent.organizationId())
                    .branchId(stockEvent.branchId())
                    .productId(stockEvent.productId())
                    .quantity(stockEvent.quantity())
                    .unitCost(stockEvent.unitCost())
                    .sourceType("PURCHASE")
                    .sourceId(stockEvent.purchaseId())
                    .build());
            log.info("Processed StockReceived event for product {}", stockEvent.productId());
        } catch (Exception ex) {
            log.warn("Failed to process platform event: {}", ex.getMessage());
        }
    }
}
