package com.mishanin.controllers;

import com.mishanin.model.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Log4j2
@Controller
@RequestMapping(Urls.HelloWorld.MAIN)
public class HelloWorldController {

    @GetMapping
    @ResponseBody
    private String hello() {
        return "Hello my friend";
    }

    @GetMapping(Urls.HelloWorld.PRODUCT)
    @ResponseStatus(OK)
    private void getProduct(@RequestBody(required = false) Product product) {
        log.info(product);
    }
}
