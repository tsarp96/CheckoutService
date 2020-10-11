package com.trendyol.checkout.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Product {
    private String id;
    private String name;
    private String detail;
    private String category;
    private String variant;
}
