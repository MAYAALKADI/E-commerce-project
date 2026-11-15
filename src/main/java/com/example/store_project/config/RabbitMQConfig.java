package com.example.store_project.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // الطابور الرئيسي
    public static final String EXCHANGE    = "order.exchange";
    public static final String QUEUE       = "order.created.queue";
    public static final String ROUTING_KEY = "order.created";

    // طابور الرسائل الفاشلة (Dead Letter)
    public static final String DLX         = "order.dlx";
    public static final String DLQ         = "order.created.dlq";
    public static final String DLQ_KEY     = "order.created.dead";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE, true, false); // durable
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(DLX)          // لو انرفضت الرسالة روح على DLX
                .deadLetterRoutingKey(DLQ_KEY)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(DLQ_KEY);
    }

    // تحويل الحدث إلى JSON بدل Java serialization
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}