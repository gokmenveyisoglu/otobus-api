package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.OrderRepository;
import com.gokmen.otobusapi.repository.UrunRepository;
import com.gokmen.otobusapi.repository.entities.OrderList;
import com.gokmen.otobusapi.repository.entities.Urun;
import com.gokmen.otobusapi.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    UrunRepository urunRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UrunRepository urunRepository) {
        this.orderRepository = orderRepository;
        this.urunRepository = urunRepository;
    }

    @Override
    public void setOrder(List<OrderList> orderList) {

        AtomicInteger numberOfGiyim = new AtomicInteger();
        AtomicInteger numberOfUrun = new AtomicInteger();
        AtomicInteger supTotalOfNonGiyim = new AtomicInteger();
        AtomicInteger subTotalOfGiyim = new AtomicInteger();
        AtomicInteger totalOfGiyim = new AtomicInteger();
        AtomicInteger total = new AtomicInteger();
        List<Urun> urunList = new ArrayList<>();

        orderList.forEach(order1 -> {
            order1.getUruns().forEach(urun -> {
                urun = urunRepository.findUrunByName(urun.getUrunAdi());
                urunList.add(urun);
                order1.setUruns(urunList);
                orderRepository.save(order1);

                if (urunRepository.findUrunByName(urun.getUrunAdi()).getKatagories().getType().equals("Giyim")) {
                    numberOfGiyim.set(numberOfGiyim.get() + 1);
                    subTotalOfGiyim.set(subTotalOfGiyim.get() + urunRepository.findUrunByName(urun.getUrunAdi()).getPrice());
                    numberOfUrun.set(numberOfUrun.get() + 1);
                } else {
                    supTotalOfNonGiyim.set(supTotalOfNonGiyim.get() + urunRepository.findUrunByName(urun.getUrunAdi()).getPrice());
                    numberOfUrun.set(numberOfUrun.get() + 1);
                }
            });
        });

        System.out.println(numberOfGiyim);
        System.out.println(numberOfUrun);
        System.out.println(subTotalOfGiyim);
        System.out.println(supTotalOfNonGiyim);

        if (numberOfGiyim.get() >= 3) {
            totalOfGiyim.set(subTotalOfGiyim.get() - ((subTotalOfGiyim.get() * 15) / 100));
        }

        if (totalOfGiyim.get() + supTotalOfNonGiyim.get() >= 5000) {
            total.set((totalOfGiyim.get() + supTotalOfNonGiyim.get()) - ((totalOfGiyim.get() + supTotalOfNonGiyim.get()) * 10) / 100);
        }

        if (total.get() < 1000) {
            total.set(total.get() + 100);
        }

        orderList.forEach(orderList1 -> {
            orderList1.setSubTotalPrice(totalOfGiyim.get() + supTotalOfNonGiyim.get());
            orderList1.setOrderPrice(total.get());
        });
        System.out.println(totalOfGiyim.get() + supTotalOfNonGiyim.get());
        System.out.println(total);

        orderRepository.saveAll(orderList);
    }
}
