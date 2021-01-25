package com.mach.bff.wishlist.mapper;

import com.mach.core.model.wishlist.WishListItem;
import com.mach.core.model.wishlist.WishListResponse;
import io.sphere.sdk.products.Image;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.Attribute;
import io.sphere.sdk.shoppinglists.LineItem;
import io.sphere.sdk.shoppinglists.ShoppingList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class WishListMapper {

    private final Locale defaultLocale;

    public WishListResponse mapShoppingListToWishListResponse(ShoppingList shoppingList) {
        final List<WishListItem> wishListItems = ListUtils.emptyIfNull(shoppingList.getLineItems())
                .stream()
                .map(this::mapLineItemToWishListItem)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(WishListItem::getTrending).reversed())
                .collect(Collectors.toList());
        return WishListResponse.builder()
                .items(wishListItems)
                .id(shoppingList.getId())
                .build();
    }

    private WishListItem mapLineItemToWishListItem(LineItem lineItem) {
        ProductVariant productVariant = lineItem.getVariant();
        if (productVariant == null) {
            log.error("Line item wish list With out variant: {}", lineItem.getProductId());
            return null;
        }
        final String image = productVariant.getImages()
                .stream()
                .findFirst()
                .map(Image::getUrl).orElse(null);
        final BigDecimal trending = Optional.ofNullable(productVariant.getAttribute("general-trending"))
                .map(Attribute::getValueAsDouble)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
        return WishListItem
                .builder()
                .name(lineItem.getName().get(defaultLocale))
                .objectID(lineItem.getProductId())
                .sku(productVariant.getSku())
                .picture(image)
                .trending(trending)
                .build();
    }
}
