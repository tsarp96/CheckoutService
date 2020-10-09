package com.trendyol.checkout.controllers;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.services.CartsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts")
public class CartsController {

    private final CartsService cartsService;

    public CartsController(CartsService cartsService) {
        this.cartsService = cartsService;
    }

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartsService.createCart(cart);
        return ResponseEntity.ok().build();
    }
}
