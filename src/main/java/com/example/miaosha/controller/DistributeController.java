package com.example.miaosha.controller;

import com.example.miaosha.annotation.DistributeLimitAnno;
import com.example.miaosha.lock.DistributeLock;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@Api
public class DistributeController {

    @Autowired
    private DistributeLock lock;

    @PostMapping("/distributedLock")
    @ResponseBody
    public String distributedLock(String key, String uuid, String secondsToLock, String userId) throws Exception{
        Boolean locked = false;
        try {
            locked = lock.distributedLock(key, uuid, secondsToLock);
            if(locked) {
                log.info("userId:{} is locked - uuid:{}", userId, uuid);
                log.info("do business logic");
                TimeUnit.MICROSECONDS.sleep(3000);
            } else {
                log.info("userId:{} is did get lock - uuid:{}", userId, uuid);
            }
        } catch (Exception e) {
            log.error("error", e);
        } finally {
            if(locked) {
                lock.distributedUnlock(key, uuid);
            }
        }
        return "ok";
    }


    @PostMapping("/distributedLimit")
    @ResponseBody
    @DistributeLimitAnno(limitKey="limit", limit = 10)
    public String distributedLimit(String userId) {
        log.info(userId);
        return "ok";
    }
}
