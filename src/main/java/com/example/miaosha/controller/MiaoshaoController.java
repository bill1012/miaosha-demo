package com.example.miaosha.controller;

import com.example.miaosha.annotation.DistributeLimitAnno;
import com.example.miaosha.aspect.LimitAspect;
import com.example.miaosha.lock.DistributeLock;
import com.example.miaosha.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.service.ResponseMessage;

@Slf4j
@RestController
@Api("miaosha controller")
public class MiaoshaoController {

    @Autowired
    OrderService orderService;

    @Autowired
    DistributeLock distributeLock;

    @Autowired
    LimitAspect limitAspect;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_PRE = "LOCK_ORDER";


    @PostMapping("/initCatalog")
    @ResponseBody
    @ApiOperation(value = "Test")
    public String initCatalog()  {
        try {
            orderService.initCatalog();
        } catch (Exception e) {
            log.error("error", e);
        }

        return "init is ok";
    }


    @PostMapping("/placeOrder")
    @ResponseBody
    @DistributeLimitAnno(limitKey = "limit", limit = 10,seconds = 1)
    public Long placeOrder(Long orderId) {
        Long saleOrderId = 0L;
        boolean locked = false;
        String key = LOCK_PRE + orderId;
        String uuid = String.valueOf(orderId);
        try {
            locked = distributeLock.distributedLock(key, uuid,
                    "10" );
            if(locked) {
                //直接操作数据库
                //saleOrderId = orderService.placeOrder(orderId);
                //操作缓存 异步操作数据库
                saleOrderId = orderService.placeOrderWithQueue(orderId);
            }
            log.info("saleOrderId:{}", saleOrderId);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if(locked) {
                distributeLock.distributedUnlock(key, uuid);
            }
        }
        return saleOrderId;
    }



    @PostMapping("/saveThread")
    @ResponseBody
    public void handleCatalog(){
        orderService.handleCatalog();
    }



}
