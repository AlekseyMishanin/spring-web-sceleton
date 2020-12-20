package com.mishanin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("hello")
public class HelloWorldController {

    @GetMapping
    @ResponseBody
    private String hello() {
        return "Hello my friend";
    }
}
