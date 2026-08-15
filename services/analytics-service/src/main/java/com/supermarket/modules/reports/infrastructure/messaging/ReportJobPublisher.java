package com.supermarket.modules.reports.infrastructure.messaging;

import java.util.UUID;

public interface ReportJobPublisher {
    void publish(UUID jobId);
}
