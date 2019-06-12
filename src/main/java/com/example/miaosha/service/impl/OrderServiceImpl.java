package com.example.miaosha.service.impl;

import com.example.miaosha.mapper.CatalogMapper;
import com.example.miaosha.mapper.SalesOrderMapper;
import com.example.miaosha.model.Catalog;
import com.example.miaosha.model.SalesOrder;
import com.example.miaosha.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    public CatalogMapper catalogMapper;

    @Autowired
    private SalesOrderMapper salesOrderMapper;


    private static final String CATALOG_TOTAL = "CATALOG_TOTAL";
    private static final String CATALOG_SOLD = "CATALOG_SOLD";
    private BlockingQueue<Long> catalogs = new ArrayBlockingQueue<>(1000);

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public void initCatalog() {
        Catalog catalog = new Catalog();
        catalog.setName("mac");
        catalog.setTotal(1000L);
        catalog.setSold(0L);
        catalogMapper.insertCatalog(catalog);
        log.info("catalog:{}", catalog);
        redisTemplate.opsForValue().set(CATALOG_TOTAL + catalog.getId(), catalog.getTotal().toString());
        redisTemplate.opsForValue().set(CATALOG_SOLD + catalog.getId(), catalog.getSold().toString());
        log.info("redis value:{}", redisTemplate.opsForValue().get(CATALOG_TOTAL + catalog.getId()));
        handleCatalog();
    }

    @Override
    public void handleCatalog() {
        new Thread(() -> {
            try {
                for(;;) {
                    Long catalogId = catalogs.take();
                    if(catalogId != 0) {
                        addOrder(catalogId);
                    }
                }

            } catch (Exception e) {
                log.error("error", e);
            }
        }).start();
    }


    public Long addOrder(Long catalogId){
        Catalog catalog = catalogMapper.selectCatalog(catalogId);
        catalog.setSold(catalog.getSold() + 1);
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCid(catalogId);
        salesOrder.setName(catalog.getName());
        catalogMapper.updateCatalog(catalog);
        salesOrderMapper.insertSalesOrder(salesOrder);
        log.info("returned salesOrder.id:{}",  salesOrder.getId());
        return salesOrder.getId();
    }

    @Override
    public Long placeOrder(Long catalogId) {
        Integer total = Integer.parseInt(redisTemplate.opsForValue().get(CATALOG_TOTAL + catalogId));
        Integer sold = Integer.parseInt(redisTemplate.opsForValue().get(CATALOG_SOLD + catalogId));
        if (total.equals(sold)){
            throw new RuntimeException("ALL SOLD OUT: " + catalogId);
        }

        Long saleId=addOrder(catalogId);
        //自增
        redisTemplate.opsForValue().increment(CATALOG_SOLD + catalogId,1) ;
        return saleId;
    }

    @Override
    public Long placeOrderWithQueue(Long catalogId) {
        String totalCache = redisTemplate.opsForValue().get(CATALOG_TOTAL + catalogId);
        String soldCache = redisTemplate.opsForValue().get(CATALOG_SOLD + catalogId);
        if(totalCache == null || soldCache == null) {
            throw new RuntimeException("Not Initialized: " + catalogId);
        }

        Integer total = Integer.parseInt(totalCache);
        Integer sold = Integer.valueOf(soldCache);

        if (total.equals(sold)){
            throw new RuntimeException("ALL SOLD OUT: " + catalogId);
        }
        try {
            catalogs.put(catalogId);
        } catch (Exception e) {
            log.error("error", e);
        }

        //自增
        Long soldId = redisTemplate.opsForValue().increment(CATALOG_SOLD + catalogId,1) ;
        return soldId;
    }
}
