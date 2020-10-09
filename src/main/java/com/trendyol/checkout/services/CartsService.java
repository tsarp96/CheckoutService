package com.trendyol.checkout.services;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.repositories.CartsRepository;
import org.springframework.stereotype.Service;

@Service
public class CartsService {

    private final CartsRepository cartsRepository;

    public CartsService(CartsRepository cartsRepository) {
        this.cartsRepository = cartsRepository;
    }

    public void createCart(Cart cart) {
        cartsRepository.insert(cart);
    }
}
