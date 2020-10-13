package com.trendyol.checkout.controllers;

import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import com.trendyol.checkout.domain.Stock;
import com.trendyol.checkout.services.CartsService;
import com.trendyol.checkout.services.RestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    // 200 OK - Product exists,  Stock Available
    // 204 NoContent - Product exists , No Stock Available
    public ResponseEntity<Void> addItem(@PathVariable String cartId, @PathVariable String itemId){
        try{
            Product product = restService.getProductByIdAsObject(itemId);
            if(!restService.isStockAvailableForProductId(product.getId())){
                return ResponseEntity.noContent().build();
            }
            cartsService.addItemToCart(cartId, product);
        }catch (HttpStatusCodeException ex){
            System.out.println(ex.getRawStatusCode() + " Product does not exist !");
        }
        return ResponseEntity.ok().build();
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
