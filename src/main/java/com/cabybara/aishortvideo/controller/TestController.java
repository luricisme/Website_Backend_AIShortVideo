package com.cabybara.aishortvideo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "Test Controller")
public class TestController {
    @Operation(summary= "Summary hello world", description = "Get hello world")
    @GetMapping(value = "/hello-world")
    public String helloWorld() {
        return "Hello World!";
    }
}
