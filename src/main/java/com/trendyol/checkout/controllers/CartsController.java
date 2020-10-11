package com.trendyol.checkout.controllers;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.services.CartsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartsController {

    private final CartsService cartsService;

    public CartsController(CartsService cartsService) {
        this.cartsService = cartsService;
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
        cartsService.addItemToCart(cartId, itemId);
        return ResponseEntity.ok().build();
    }

}
