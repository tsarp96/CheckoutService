package com.trendyol.checkout.services;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.repositories.CartsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartsService {

    private final CartsRepository cartsRepository;

    public CartsService(CartsRepository cartsRepository) {
        this.cartsRepository = cartsRepository;
    }

    public void createCart(Cart cart) {
        cartsRepository.insert(cart);
    }

    public void deleteById(String cartId) {
        cartsRepository.deleteById(cartId);
    }

    public List<Cart> getCartByUserId(String userId){
        return cartsRepository.getCartByUserID(userId);
    }

    public void addItemToCart(String cartId, String itemId) {

    }

}
