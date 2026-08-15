package com.supermarket.modules.reports.infrastructure.messaging;

import com.supermarket.shared.infrastructure.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "supermarket.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class RabbitReportJobPublisher implements ReportJobPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(UUID jobId) {
        rabbitTemplate.convertAndSend(RabbitConfig.REPORT_JOBS_QUEUE, new ReportJobMessage(jobId));
    }
}
