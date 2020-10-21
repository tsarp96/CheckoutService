package com.trendyol.checkout.controllers;

import com.trendyol.checkout.DTOs.UpdateDto.*;
import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import com.trendyol.checkout.exceptions.ItemAlreadyExistInCartException;
import com.trendyol.checkout.services.CartsService;
import com.trendyol.checkout.services.RestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/carts")
public class CartsController {

    private final CartsService cartsService;
    private final RestService restService;

    public CartsController(CartsService cartsService, RestService restService) {
        this.restService = restService;
        this.cartsService = cartsService;
    }

    @PostMapping
    public ResponseEntity createCart(@RequestParam(name = "userId") String userId) {
        try {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cartsService.createCart(cart);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(cart.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(
                    "Cart Service Unavailable !",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Cart> deleteById(@PathVariable String cartId) {
        cartsService.deleteById(cartId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Cart> getCartByUserId(@RequestParam(name = "userId") String userId) {
        try{
            Cart cart = cartsService.getCartByUserId(userId);
            if (cart == null) {
                return new ResponseEntity(
                        "No Cart Found with userID: " + userId,
                        HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(cart);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return new ResponseEntity(
                    "No Cart Found with userID: " + userId,
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable String cartId) {
        try{
            Cart cart = cartsService.getCartById(cartId);
            if (cart == null) {
                return new ResponseEntity(
                        "(1) No Cart Found with ID: " + cartId,
                        HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(cart);
        }catch (Exception ex){
            return new ResponseEntity(
                    "(2) No Cart Found with ID: " + cartId,
                    HttpStatus.NOT_FOUND);
        }


    }

    @PostMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> addItem(@PathVariable String cartId, @PathVariable String itemId) {
        try{
            Product product = restService.getProductByIdAsObject(itemId);
            if (product == null) {
                return new ResponseEntity("Product is null !", HttpStatus.NO_CONTENT); }
            if (!restService.isStockAvailableForProductId(product.getId()))
                return new ResponseEntity("No Available Stock for Product !", HttpStatus.NO_CONTENT);
            cartsService.addItemToCart(cartId, product);

        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return new ResponseEntity(
                    "No stock is defined for product !",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (ItemAlreadyExistInCartException e){
            return new ResponseEntity(
                    "Item already exist in your cart !",
                    HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(
                "THE PRODUCT WAS ADDED TO YOUR CART SUCCESSFULLY !",
                HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable String cartId, @PathVariable String itemId) {
        try{
            Product product = restService.getProductByIdAsObject(itemId);
            if (product == null) {
                return new ResponseEntity(
                        "There is no such item !",
                        HttpStatus.NOT_FOUND);
            }
            Cart cart = restService.getCartByIdAsObject(cartId);
            if (!restService.isProductInCart(cart, product)) {
                return new ResponseEntity(
                        "Product is not in cart!", HttpStatus.NOT_FOUND);
            }
            cartsService.removeItemFromCart(cartId, itemId);
            return new ResponseEntity(
                    "Product: " + itemId + " is removed from cart " + cartId,
                    HttpStatus.OK);
        }catch (HttpStatusCodeException ex){
            return new ResponseEntity(
                    "Specific error handling for  : " + ex.getRawStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> updateItem(@PathVariable String cartId, @PathVariable String itemId, @RequestBody ItemQuantityDTO itemQuantityDTO) {
        Product product = restService.getProductByIdAsObject(itemId);
        Cart cart = restService.getCartByIdAsObject(cartId);

        if (product == null) {
            return new ResponseEntity(
                    "There is no such item !",
                    HttpStatus.NO_CONTENT);
        }
        if (!restService.isStockAvailableForProductId(itemId)){
            return new ResponseEntity(
                    "There are not enough products in stock!",
                    HttpStatus.NO_CONTENT);
        }
        if (!restService.isProductInCart(cart, product)) {
            return new ResponseEntity(
                    "Product is not in cart!", HttpStatus.NO_CONTENT);
        }
        product.setQuantity(itemQuantityDTO.getQuantity());
        cartsService.updateItemQuantity(cartId, product);
        return new ResponseEntity(
                "The quantity of the product has been updated!",
                HttpStatus.OK);

    }

}
