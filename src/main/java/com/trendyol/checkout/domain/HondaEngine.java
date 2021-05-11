package com.trendyol.checkout.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HondaEngine implements Engine {

    private String id ;

    @Override
    public void Start() {
        System.out.println("Honda Engine is started !");
    }
}
