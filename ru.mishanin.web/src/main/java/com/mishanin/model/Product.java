package com.mishanin.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Test product
 */
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("price")
    private final BigDecimal pricePerOne;
    @JsonProperty("date_added")
    private final Date dateAdded;

    @JsonCreator
    public Product(@JsonProperty("name") String name,
                   @JsonProperty("price") BigDecimal pricePerOne,
                   @JsonProperty("date_added") Date dateAdded) {
        this.name = name;
        this.pricePerOne = pricePerOne;
        this.dateAdded = dateAdded;
    }
}
