package com.project.orders.service;

import com.project.orders.config.PublishEvent;
import com.project.orders.dtos.OrderDTO;
import com.project.orders.model.Product;
import com.project.orders.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PublishEvent publishEvent;

    public void placeOrder(OrderDTO order) {
        logger.info("placing order {}", order);
        Product product=productRepository.findById(order.getId()).
                orElseThrow(()-> new RuntimeException("Product not found"));
        int checkQuantity=product.getQuantity()-order.getQuantity();
        logger.info("product is {}",product.getId());
        if (checkQuantity<0){
            throw new RuntimeException("Quantity out of stock");
        }
        publishEvent.sendOrder(order);
        logger.info("order placed");
    }
}
