package com.mach.bff.wishlist.controller;

import com.mach.bff.wishlist.service.WishListService;
import com.mach.core.model.wishlist.WishListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wish-lists")
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;

    @PutMapping("/products/{productId}/variant/{variantId}")
    public ResponseEntity<WishListResponse> addProductToShoppingList(@PathVariable("productId") String productId,
                                                           @PathVariable("productId") String variantId) {
        return ResponseEntity.ok(wishListService.addProductFromWishList(productId));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<WishListResponse> removeProductFromShoppingList(@PathVariable("productId") String productId) {
        return ResponseEntity.ok(wishListService.removeProductFromWishList(productId));
    }
}
