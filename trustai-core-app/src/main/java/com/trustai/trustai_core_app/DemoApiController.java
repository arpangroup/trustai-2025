package com.trustai.trustai_core_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoApiController {
    @GetMapping("/ping")
    public String ping() {
        return "Hello World";
    }
}
