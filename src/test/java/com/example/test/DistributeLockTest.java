package com.example.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class DistributeLockTest {


    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void distrubtedLock() {
        String url = "http://localhost:8081/distributedLock";
        String uuid = "abcdefg";
//        log.info("uuid:{}", uuid);
        String key = "redisLock";
        String secondsToLive = "1";

        for (int i = 0; i < 1000; i++) {
            final int userId = i;
            new Thread(() -> {
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("uuid", uuid);
                params.add("key", key);
                params.add("secondsToLock", secondsToLive);
                params.add("userId", String.valueOf(userId));
                String result = restTemplate.postForObject(url, params, String.class);
                System.out.println("-------------" + result);
             }
            ).start();
        }
    }



    @Test
    public void distrubtedLimit() {
        String url = "http://localhost:8081/distributedLimit";


        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                String userId = UUID.randomUUID().toString();
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("userId", userId);
                String result = restTemplate.postForObject(url, params, String.class);
                System.out.println("-------------" + result);
            }
            ).start();
        }
    }
}
