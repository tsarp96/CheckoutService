package com.trendyol.checkout.controllers;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import com.trendyol.checkout.services.CartsService;
import com.trendyol.checkout.services.RestService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> createCart(@RequestBody Cart cart) {
        cartsService.createCart(cart);
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

    @PostMapping("/{cartId}/items/{itemId}")
    public ResponseEntity addItem(@PathVariable String cartId, @PathVariable String itemId){
        //TODO: GET Product Service with productId
        //TODO: Check case : productId does not exist
        try{
            Product product = restService.getProductByIdAsObject(itemId);
        }catch (HttpStatusCodeException ex){

        }
        //TODO: GET Stock Service /products/product-id/stocks - check for stocks
        //TODO: According to http response return an exception or success response -- Error handling
        //---------------------------------------------
        cartsService.addItemToCart(cartId, itemId);
        return ResponseEntity.ok().build();
    }

}
