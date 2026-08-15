package com.supermarket.modules.reports.infrastructure.messaging;

import com.supermarket.modules.reports.application.ReportGeneratorService;
import com.supermarket.shared.infrastructure.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "supermarket.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class ReportJobListener {

    private final ReportGeneratorService reportGeneratorService;

    @RabbitListener(queues = RabbitConfig.REPORT_JOBS_QUEUE)
    public void processReportJob(ReportJobMessage message) {
        reportGeneratorService.processJob(message.jobId());
    }
}
