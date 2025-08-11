package com.trustai.trustai_core_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class PingApiController {

    @GetMapping("/ping")
    public String ping() {
        return "Hello World";
    }
}
