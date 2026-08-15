package com.supermarket.shared.infrastructure;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "supermarket.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitConfig {

    public static final String PLATFORM_EVENTS_QUEUE = "platform.events";
    public static final String REPORT_JOBS_QUEUE = "report.jobs";

    @Value("${supermarket.rabbitmq.platform-events-queue:" + PLATFORM_EVENTS_QUEUE + "}")
    private String platformEventsQueue;

    @Value("${supermarket.rabbitmq.report-jobs-queue:" + REPORT_JOBS_QUEUE + "}")
    private String reportJobsQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public Queue platformEventsQueue() {
        return new Queue(platformEventsQueue, true);
    }

    @Bean
    public Queue reportJobsQueue() {
        return new Queue(reportJobsQueue, true);
    }
}
