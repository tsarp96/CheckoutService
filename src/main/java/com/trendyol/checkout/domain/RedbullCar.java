package com.trendyol.checkout.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class RedbullCar implements Car {

    @Autowired
    private Engine Engine;

    private String id;

    @Override
    public void Drive() {
        System.out.println("RedbullCar Mode: Drive");
    }

}
