package com.supermarket.modules.reports.infrastructure.messaging;

import com.supermarket.modules.reports.application.ReportGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "supermarket.rabbitmq.enabled", havingValue = "false")
@RequiredArgsConstructor
public class InlineReportJobPublisher implements ReportJobPublisher {

    private final ReportGeneratorService reportGeneratorService;

    @Override
    public void publish(UUID jobId) {
        reportGeneratorService.processJob(jobId);
    }
}
