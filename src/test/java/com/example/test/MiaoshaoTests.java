package com.example.test;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class MiaoshaoTests {


    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void miaoshao() {

        String url = "http://localhost:8081/placeOrder";
        for (int i = 0; i < 30000; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
                new Thread(() -> {
                    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                    params.add("orderId", "4");
                    Long result = testRestTemplate.postForObject(url, params, Long.class);
                    if (result != 0) {
                        System.out.println("-------------" + result);
                    }
                }
                ).start();
            } catch (Exception e) {
                log.info("error:{}", e.getMessage());
            }

        }
    }


    @Test
    public void startThread() {
        try {
            String url = "http://localhost:8081/initCatalog";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            testRestTemplate.postForObject(url, params, Void.class);
        } catch (Exception e) {
            log.info("error:{}", e.getMessage());
        }
    }
}

