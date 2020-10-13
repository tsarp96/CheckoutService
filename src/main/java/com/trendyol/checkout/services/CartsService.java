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
    public Cart createCart(Cart cart) {
        try{
            cartsRepository.insert(cart);
            return cart;
        }catch (Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public void deleteById(String cartId) {
        cartsRepository.deleteById(cartId);
    }

    public List<Cart> getCartByUserId(String userId){
        return cartsRepository.getCartByUserID(userId);
    }

    public void addItemToCart(String cartId, Product product) {
        System.out.println("add item");
    }

}
