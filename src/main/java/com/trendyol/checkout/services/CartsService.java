package com.trendyol.checkout.services;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import com.trendyol.checkout.repositories.CartsRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartsService {

    private final CartsRepository cartsRepository;

    public CartsService(CartsRepository cartsRepository) {
        this.cartsRepository = cartsRepository;
    }

    @Async
    public void createCart(Cart cart) {
        cartsRepository.insert(cart);
    }

    public void deleteById(String cartId) {
        cartsRepository.deleteById(cartId);
    }

    public Cart getCartByUserId(String userId){
        return cartsRepository.getCartByUserID(userId);
    }

    public void addItemToCart(String cartId, Product product) {
        cartsRepository.addItemToCart(cartId, product);
    }

    public Cart getCartById(String cartId) {
        return cartsRepository.findById(cartId);
    }
}
