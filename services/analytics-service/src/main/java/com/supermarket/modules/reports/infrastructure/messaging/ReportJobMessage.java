package com.supermarket.modules.reports.infrastructure.messaging;

import java.util.UUID;

public record ReportJobMessage(UUID jobId) {
}
