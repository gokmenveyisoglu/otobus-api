package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.OrderList;
import com.gokmen.otobusapi.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("order")
@Tag(name = "Order", description = "Test amaçlıdır")
public class OrderContoller {

    OrderService orderService;

    OrderContoller(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    void giveOrder(@RequestBody List<OrderList> orderLists) {
        orderService.setOrder(orderLists);
    }
}
