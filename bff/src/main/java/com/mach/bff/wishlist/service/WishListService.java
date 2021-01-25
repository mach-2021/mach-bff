package com.mach.bff.wishlist.service;

import com.mach.bff.catalog.repository.ProductRepository;
import com.mach.bff.security.model.UserPrincipal;
import com.mach.bff.security.util.SecurityUtils;
import com.mach.bff.wishlist.mapper.WishListMapper;
import com.mach.bff.wishlist.repository.WishListRepository;
import com.mach.core.exception.NotFoundException;
import com.mach.core.exception.UnprocessableEntityException;
import com.mach.core.model.wishlist.WishListResponse;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.shoppinglists.LineItem;
import io.sphere.sdk.shoppinglists.ShoppingList;
import io.sphere.sdk.shoppinglists.commands.updateactions.AddLineItem;
import io.sphere.sdk.shoppinglists.commands.updateactions.RemoveLineItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishListService {
    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private final WishListMapper wishListMapper;

    public WishListResponse createCustomerWishList(String customerId) {
        log.info("Start create wish list for customer {}", customerId);
        ShoppingList shoppingList = wishListRepository.createWishList(customerId).toCompletableFuture().join();
        return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
    }

    public WishListResponse findCustomerWishList() {
        final UserPrincipal userPrincipal = SecurityUtils.getUserPrincipal();
        log.info("Start create wish list for customer {}", userPrincipal.getInternalId());
        return findCustomerWishList(userPrincipal.getInternalId());
    }

    public WishListResponse findCustomerWishList(String customerId) {
        log.info("Start create wish list for customer {}", customerId);
        ShoppingList shoppingList = findShoppingList(customerId);
        return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
    }

    public WishListResponse removeProductFromWishList(String productId) {
        final UserPrincipal userPrincipal = SecurityUtils.getUserPrincipal();
        String customerId = userPrincipal.getInternalId();
        log.info("Start remove sku {} from customer {} wish list", productId, customerId);
        ShoppingList shoppingList = findShoppingList(customerId);
        final Optional<LineItem> lineItem = findItemInShoppingList(shoppingList, productId);
        if (lineItem.isEmpty()) {
            return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
        }
        final RemoveLineItem removeLineItem = RemoveLineItem.of(lineItem.get());
        shoppingList = wishListRepository.updateWishList(shoppingList, removeLineItem)
                .toCompletableFuture().join();
        return wishListMapper.mapShoppingListToWishListResponse(shoppingList);
    }

    public WishListResponse addProductToWishList(String productId) {
        final UserPrincipal userPrincipal = SecurityUtils.getUserPrincipal();
        String customerId = userPrincipal.getInternalId();
        log.info("Start add productId {} to customer {} wish list", productId, customerId);
        ShoppingList shoppingList = findShoppingList(customerId);
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
                .orElseThrow(() -> new NotFoundException("Wish list does not exist", "wish_list_not_exists"));
    }

    private Optional<LineItem> findItemInShoppingList(ShoppingList shoppingList, String productId) {
        return ListUtils.emptyIfNull(shoppingList.getLineItems()).stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }
}
