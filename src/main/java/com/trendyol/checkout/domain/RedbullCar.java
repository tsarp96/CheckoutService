package com.trendyol.checkout.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Getter
@Setter
public class RedbullCar implements Car {

    private String id;
    private Engine engine;

    public RedbullCar(Engine engine){
        this.engine = engine;
    }
    @Override
    public void Drive() {
        System.out.println("RedbullCar Mode: Drive");
    }
}

