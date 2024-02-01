package com.jb.greetapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "WELCOME_API")
public interface WelcomeApiClient {

    @GetMapping("/welcome")
    public String getWelcomeMsg();
}
