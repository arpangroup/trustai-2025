package com.trustai.trustai_core_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping("/home")
    public String home() {
        return "index";
    }
}
