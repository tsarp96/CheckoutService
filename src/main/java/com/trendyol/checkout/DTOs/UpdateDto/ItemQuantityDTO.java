package com.trendyol.checkout.DTOs.UpdateDto;

import lombok.Getter;
import lombok.Setter;

public class ItemQuantityDTO {
    private int quantity;

    public ItemQuantityDTO(){ }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
