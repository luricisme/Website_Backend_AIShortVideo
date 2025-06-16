package com.cabybara.aishortvideo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {
    @GetMapping(value = "/hello-world")
    public String helloWorld() {
        return "Hello World!";
    }
}
