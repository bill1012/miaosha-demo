package com.example.miaosha.service;

public interface OrderService {

    void initCatalog();

    Long placeOrder(Long catalogId);

    Long placeOrderWithQueue(Long catalogId);

     void handleCatalog();

}
