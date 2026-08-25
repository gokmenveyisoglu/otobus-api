package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.OrderList;

import java.util.List;

public interface OrderService {

    void setOrder(List<OrderList> orderList);
}
