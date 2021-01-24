package com.mach.bff.wishlist.service;

import com.mach.bff.wishlist.mapper.WishListMapper;
import com.mach.bff.wishlist.repository.WishListRepository;
import com.mach.core.model.wishlist.WishListResponse;
import io.sphere.sdk.shoppinglists.LineItem;
import io.sphere.sdk.shoppinglists.ShoppingList;
import io.sphere.sdk.shoppinglists.commands.updateactions.AddLineItem;
import io.sphere.sdk.shoppinglists.commands.updateactions.RemoveLineItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishListService {
    private final WishListRepository wishListRepository;
    private final WishListMapper wishListMapper;

    public WishListResponse createCustomerWishList(String customerId) {
        log.info("Start create wish list for customer {}", customerId);
        ShoppingList shoppingList = wishListRepository.createWishList(customerId).toCompletableFuture().join();
        return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
    }

    public WishListResponse removeProductFromWishList(String productId) {
        log.info("Start remove product {} from wish list", productId);
        ShoppingList shoppingList = findShoppingList("customerId");
        final Optional<LineItem> lineItem = findItemInShoppingList(shoppingList, productId);
        if (lineItem.isEmpty()) {
            return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
        }
        final RemoveLineItem removeLineItem = RemoveLineItem.of(lineItem.get());
        shoppingList = wishListRepository.updateWishList(shoppingList, removeLineItem)
                .toCompletableFuture().join();
        return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
    }

    public WishListResponse addProductFromWishList(String productId) {
        log.info("Start add product {} to wish list", productId);
        ShoppingList shoppingList = findShoppingList("customerId");
        final Optional<LineItem> lineItem = findItemInShoppingList(shoppingList, productId);
        if (lineItem.isPresent()) {
            log.error("Product {} already exists in wish list", productId);
            return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
        }
        final AddLineItem addLineItem = AddLineItem.of(productId).withQuantity(1L);
        shoppingList = wishListRepository.updateWishList(shoppingList, addLineItem)
                .toCompletableFuture().join();
        return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
    }

    private ShoppingList findShoppingList(String customerId) {
        return wishListRepository.findCustomerWishList(customerId)
                .toCompletableFuture().join().getResults()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Optional<LineItem> findItemInShoppingList(ShoppingList shoppingList, String productId) {
        return ListUtils.emptyIfNull(shoppingList.getLineItems()).stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst();
    }
}
