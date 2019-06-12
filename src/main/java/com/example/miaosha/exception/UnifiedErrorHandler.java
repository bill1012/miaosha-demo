package com.example.miaosha.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class UnifiedErrorHandler {
    private static Map<String, String> res = new HashMap<>(2);

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object processException(HttpServletRequest req, Exception e) {
        res.put("url", req.getRequestURL().toString());

        if(e instanceof RuntimeException) {
            res.put("mess", e.getMessage());
        } else {
            res.put("mess", "sorry error happens");
        }
        return res;
    }

}

