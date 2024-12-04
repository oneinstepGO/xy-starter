package com.oneinstep.starter.api.controller;

import com.oneinstep.starter.core.limit.distribute.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
@Validated
public class TestController {

    @GetMapping("rateLimit")
    @RateLimit(value = 3, algorithm = RateLimit.Algorithm.TOKEN_BUCKET, scope = RateLimit.RateScope.CLUSTER)
    public String rateLimit33() {
        return "OK";
    }
}
