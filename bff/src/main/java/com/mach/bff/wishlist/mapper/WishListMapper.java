package com.mach.bff.wishlist.mapper;

import com.mach.core.model.wishlist.WishListItem;
import com.mach.core.model.wishlist.WishListResponse;
import io.sphere.sdk.shoppinglists.LineItem;
import io.sphere.sdk.shoppinglists.ShoppingList;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WishListMapper {

    private final Locale defaultLocale;

    public WishListResponse mapShoppingListToWishListResponse(ShoppingList shoppingList) {
        final List<WishListItem> wishListItems = ListUtils.emptyIfNull(shoppingList.getLineItems())
                .stream()
                .map(this::mapLineItemToWishListItem)
                .collect(Collectors.toList());
        return WishListResponse.builder()
                .items(wishListItems)
                .id(shoppingList.getId())
                .build();
    }

    private WishListItem mapLineItemToWishListItem(LineItem lineItem) {
        return WishListItem
                .builder()
                .name(lineItem.getName().get(defaultLocale))
                .productId(lineItem.getProductId())
                .build();
    }
}
