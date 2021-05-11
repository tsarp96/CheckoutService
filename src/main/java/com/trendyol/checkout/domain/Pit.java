package com.trendyol.checkout.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Pit {
    private String PitId;
    private String Owner;
    private List<Car> Cars;

    public Pit(){
        Cars = new ArrayList<>();
    }
}
