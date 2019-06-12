package com.example.miaosha.limit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class DistributeLimit {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisScript<Long> limitScript;

    @Autowired
    RedisScript<Long> rateLimitScript;

    public Boolean distributedLimit(String key, String limit) {
        Long id = 0L;

        try {
            id = redisTemplate.execute(limitScript, Collections.singletonList(key), limit);
            log.info("id:{}", id);
        } catch (Exception e) {
            log.error("error", e);
        }

        if (id == 0L) {
            return false;
        } else {
            return true;
        }
    }


    public Boolean distributedRateLimit(String key, String limit, String seconds) {
        Long id = 0L;
        long intervalInMills = Long.valueOf(seconds) * 1000;
        long limitInLong = Long.valueOf(limit);
        long intervalPerPermit = intervalInMills / limitInLong;
        try {
            id = redisTemplate.execute(rateLimitScript, Collections.singletonList(key),
                    String.valueOf(intervalPerPermit), String.valueOf(System.currentTimeMillis()),
                    String.valueOf(limitInLong), String.valueOf(intervalInMills));
        } catch (Exception e) {
            log.error("error", e);
        }

        if (id == 0L) {
            return false;
        } else {
            return true;
        }
    }

}
