package com.trendyol.checkout.controllers;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import com.trendyol.checkout.domain.Stock;
import com.trendyol.checkout.services.CartsService;
import com.trendyol.checkout.services.RestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartsController {

    private final CartsService cartsService;
    private final RestService restService;

    public CartsController(CartsService cartsService, RestService restService) {
        this.restService = restService;
        this.cartsService = cartsService;
    }

    @GetMapping("/testrestGetProductPlainJSON")
    public ResponseEntity<String> testRest(){
        return  ResponseEntity.ok(restService.getPostsPlainJSON());
    }

    @GetMapping("/testrestGetProductAsObject")
    public ResponseEntity<Product> testRest2(){
        return  ResponseEntity.ok(restService.getProductAsObject());
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
    public ResponseEntity<List<Cart>> getCartByUserId(@RequestParam(name = "userId") String userId){
        List<Cart> carts = cartsService.getCartByUserId(userId);
        return ResponseEntity.ok(carts);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable String cartId){
        Cart cart = cartsService.getCartById(cartId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> addItem(@PathVariable String cartId, @PathVariable String itemId){
        try{
            Product product = restService.getProductByIdAsObject(itemId);
            if(product == null){
                return new ResponseEntity(
                        "There is no such item !",
                        HttpStatus.NO_CONTENT);
            }
            if(!restService.isStockAvailableForProductId(product.getId())){
                return new ResponseEntity(
                        "No Available Stock for Product !",
                        HttpStatus.NO_CONTENT);
            }
            cartsService.addItemToCart(cartId, product);
        }catch (HttpStatusCodeException ex){
            System.out.println(ex.getRawStatusCode());
            return new ResponseEntity(
                    "PRODUCT DOES NOT EXIST !",
                    HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(
                "THE PRODUCT WAS ADDED TO YOUR CART SUCCESSFULLY !",
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
