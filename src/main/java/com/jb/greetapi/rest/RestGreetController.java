package com.jb.greetapi.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestGreetController {

    @GetMapping("/greet")
    public String greetAPI(){
        return "Good Morning";
    }
}
