package com.mishanin.model;

import com.mishanin.service.SampleService;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(autowire = Autowire.BY_TYPE, dependencyCheck = true, preConstruction = true)
public class ConfigurableApple {

    @Autowired
    private SampleService sampleService;

    public String getAppleSerialNumber() {
        return sampleService.getRandomString();
    }
}
