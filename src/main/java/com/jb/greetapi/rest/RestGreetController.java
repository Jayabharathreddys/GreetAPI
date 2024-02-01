package com.jb.greetapi.rest;

import com.jb.greetapi.client.WelcomeApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestGreetController {

    @Autowired
    WelcomeApiClient welcomeApiClient;


    @GetMapping("/greet")
    public String greetAPI(){
        String msg = welcomeApiClient.getWelcomeMsg();

        return "Good Morning".concat(msg);
    }
}
