package com.trendyol.checkout.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Cart {
    //private final String id;
    private String id;
    private List<Product> products;
    private double cartPrice;
    private String userId;
    private double discountRatio;
    public Cart(){
        this.id = UUID.randomUUID().toString();
    }


}
