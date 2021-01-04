package com.mishanin.controllers;

import com.mishanin.model.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Log4j2
@Controller
@RequestMapping(Urls.HelloWorld.MAIN)
public class HelloWorldController {

    @Value("${user.name}")
    private String username;

    @GetMapping
    @ResponseBody
    private String hello() {
        return "Hi from " + username;
    }

    @GetMapping(Urls.HelloWorld.PRODUCT)
    @ResponseStatus(OK)
    private void getProduct(@RequestBody(required = false) Product product) {
        log.info(product);
    }
}
