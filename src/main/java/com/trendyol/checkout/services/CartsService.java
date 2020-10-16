package com.trendyol.checkout.services;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import com.trendyol.checkout.exceptions.ItemAlreadyExistInCartException;
import com.trendyol.checkout.repositories.CartsRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    public void addItemToCart(String cartId, Product productToBeAdd) throws ItemAlreadyExistInCartException {
        Cart cart = cartsRepository.findById(cartId);
        for (Product productInCart :
                cart.getProducts()) {
            if(productInCart.getId().equals(productToBeAdd.getId())){
                throw new ItemAlreadyExistInCartException();
            }
        }
        cartsRepository.addItemToCart(cartId, productToBeAdd);
    }

    public Cart getCartById(String cartId) {
        return cartsRepository.findById(cartId);
    }
    public void removeItemFromCart(String cartId, String itemId) {
        cartsRepository.removeItemFromCart(cartId, itemId);
    }


    public void updateItemQuantity(String cartId, Product product) {
        try {
            cartsRepository.updateProduct(cartId, product);
        } catch (Exception e) {
            throw new IllegalArgumentException(); // check again
        }
    }
}
