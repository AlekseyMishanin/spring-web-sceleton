package com.mishanin.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class SampleServiceImpl implements SampleService {

    @Override
    public String getRandomString() {
        return RandomStringUtils.randomAlphabetic(7);
    }
}
