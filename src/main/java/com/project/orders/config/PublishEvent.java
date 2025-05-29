package com.project.orders.config;

import com.project.orders.dtos.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class PublishEvent {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendOrder(OrderDTO orderDTO) {
        rabbitTemplate.convertAndSend("order.exchange","order.queue",orderDTO);
        logger.info("Order Published {}", orderDTO);
    }
}
