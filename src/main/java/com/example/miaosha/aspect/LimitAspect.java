package com.example.miaosha.aspect;

import com.example.miaosha.annotation.DistributeLimitAnno;
import com.example.miaosha.limit.DistributeLimit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LimitAspect {

    @Autowired
    DistributeLimit distributeLimit;

    @Pointcut("@annotation(com.example.miaosha.annotation.DistributeLimitAnno)")
    public void limit() {};

    @Before("limit()")
    public void beforeLimit(JoinPoint joinPoint) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributeLimitAnno distriLimitAnno = method.getAnnotation(DistributeLimitAnno.class);
        String key = distriLimitAnno.limitKey();
        int limit = distriLimitAnno.limit();
        int seconds=distriLimitAnno.seconds();
        //　通过计数限流，容易瞬间达到峰值
        //Boolean exceededLimit = distributeLimit.distributedLimit(key, String.valueOf(limit));
        //　通过令牌桶方式，过度更平滑
        Boolean exceededLimit=distributeLimit.distributedRateLimit(key,String.valueOf(limit),String.valueOf(seconds));
        if(!exceededLimit) {
            throw new RuntimeException("exceeded limit");
        }
    }

}