package com.trendyol.checkout.controllers;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import com.trendyol.checkout.exceptions.ItemAlreadyExistInCartException;
import com.trendyol.checkout.exceptions.NoAvailableStockForRequestedProductException;
import com.trendyol.checkout.services.CartsService;
import com.trendyol.checkout.services.RestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity createCart(@RequestBody Cart cart) {
        try{
            cartsService.createCart(cart);
        }catch (RuntimeException ex){
            return new ResponseEntity<>(
                    "Something went wrong on our side !",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cart.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Cart> deleteById(@PathVariable String  cartId){
        cartsService.deleteById(cartId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Cart> getCartByUserId(@RequestParam(name = "userId") String userId){
        Cart cart = cartsService.getCartByUserId(userId);
        if(cart == null){
            return new ResponseEntity(
                    "No Cart Found with userID: " + userId,
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable String cartId){
        Cart cart = cartsService.getCartById(cartId);
        if(cart == null){
            return new ResponseEntity(
                    "No Cart Found with ID: " + cartId,
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> addItem(@PathVariable String cartId, @PathVariable String itemId){
            Product product = restService.getProductByIdAsObject(itemId);
            if(product == null){
                return new ResponseEntity(
                        "There is no such item !",
                        HttpStatus.NO_CONTENT);
            }
            if(!restService.isStockAvailableForProductId(product.getId())) return new ResponseEntity(
                    "No Available Stock for Product !",
                    HttpStatus.NO_CONTENT);
            try{
                cartsService.addItemToCart(cartId, product);
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
    public ResponseEntity<Void>deleteItem(@PathVariable String cartId, @PathVariable String itemId) {
        Product product = restService.getProductByIdAsObject(itemId);
        Cart cart = restService.getCartByIdAsObject(cartId);
        if (product == null) {
            return new ResponseEntity(
                    "There is no such item !",
                    HttpStatus.NO_CONTENT);
        }
        if(!restService.isProductInCart(cart,product))
        {return new ResponseEntity(
                "Product is not in cart!",HttpStatus.NO_CONTENT);}
        cartsService.removeItemFromCart(cartId, itemId);
        return new ResponseEntity(
                "Product: " + itemId + " is removed from cart " + cartId,
                HttpStatus.OK);
    }




    //TODO: GET CART REQUEST /carts/{cartsId}
        //TODO:  GET PRODUCTS  /products/
        //TODO:  GET STOCKS -- stock control
    //TODO: DELETE ITEM
        //TODO: GET PRODUCT
        //TODO: STOCK CONTROL
    //TODO: UPDATE ITEM (CHANGE QUANTITY)
        //TODO: GET PRODUCT
        //TODO: GET STOCKS
        // ARRANGE CART - UPDATE CART
}
