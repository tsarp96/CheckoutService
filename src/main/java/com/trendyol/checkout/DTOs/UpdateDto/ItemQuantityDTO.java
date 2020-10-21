package com.trendyol.checkout.DTOs.UpdateDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemQuantityDTO {
    private Integer quantity;

    protected ItemQuantityDTO(){ }
}
