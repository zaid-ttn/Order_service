package com.project.orders.controller;

import com.project.orders.dtos.OrderDTO;
import com.project.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<String> placeOrder(@RequestBody OrderDTO order) {
        orderService.placeOrder(order);
        return new ResponseEntity<>("Order placed successfully", HttpStatus.OK);
    }

}
