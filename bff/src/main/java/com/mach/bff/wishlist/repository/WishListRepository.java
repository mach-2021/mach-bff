package com.mach.bff.wishlist.repository;

import com.mach.commercetools.repository.BaseRepository;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.models.EnumValue;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.ResourceIdentifier;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.shoppinglists.ShoppingList;
import io.sphere.sdk.shoppinglists.ShoppingListDraft;
import io.sphere.sdk.shoppinglists.ShoppingListDraftBuilder;
import io.sphere.sdk.shoppinglists.commands.ShoppingListCreateCommand;
import io.sphere.sdk.shoppinglists.commands.ShoppingListUpdateCommand;
import io.sphere.sdk.shoppinglists.expansion.ShoppingListExpansionModel;
import io.sphere.sdk.shoppinglists.queries.ShoppingListQuery;
import io.sphere.sdk.types.CustomFieldsDraft;
import io.sphere.sdk.types.CustomFieldsDraftBuilder;
import io.sphere.sdk.types.EnumFieldType;
import io.sphere.sdk.types.FieldDefinition;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.TypeDraft;
import io.sphere.sdk.types.TypeDraftBuilder;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletionStage;

@Repository
@RequiredArgsConstructor
public class WishListRepository {
    private static final String LIST_TYPE_FIELD = "type";
    private static final String LIST_FAVOURITE_TYPE = "favourite";
    private final BaseRepository repository;

    public CompletionStage<ShoppingList> createWishList(String customerId) {
        final CustomFieldsDraft fieldsDraft = CustomFieldsDraftBuilder.ofTypeKey("shoppingListType")
                .addObject(LIST_TYPE_FIELD, LIST_FAVOURITE_TYPE).build();
        final ShoppingListDraft shoppingListDraft =
                ShoppingListDraftBuilder.of(LocalizedString.of(Locale.US, "Favourite list"))
                        .custom(fieldsDraft)
                        .customer(ResourceIdentifier.ofId(customerId))
                        .build();
        return repository.executeWithThrowing(ShoppingListCreateCommand.of(shoppingListDraft)
                .plusExpansionPaths(ShoppingListExpansionModel::customer)
                .plusExpansionPaths(ShoppingListExpansionModel::lineItems));
    }

    public CompletionStage<PagedQueryResult<ShoppingList>> findCustomerWishList(String customerId) {
        return repository.executeWithThrowing(ShoppingListQuery.of()
                .plusPredicates(shoppingList -> shoppingList.customer().id().is(customerId))
                .plusPredicates(shoppingList -> shoppingList.custom().fields().ofEnum(LIST_TYPE_FIELD).key().is(LIST_FAVOURITE_TYPE))
                .plusExpansionPaths(ShoppingListExpansionModel::customer)
                .plusExpansionPaths(ShoppingListExpansionModel::lineItems));
    }

    public void createCustomType() {
        final FieldDefinition fieldDefinition = FieldDefinition.of(EnumFieldType.of(List.of(EnumValue.of("favourite", "Favourite"))),
                "type", LocalizedString.of(Locale.US, "List type"), true);
        final TypeDraft typeDraft = TypeDraftBuilder.of("shoppingListType", LocalizedString.of(Locale.US, "Shopping list addition fields"), Set.of("shopping-list"))
                .fieldDefinitions(List.of(fieldDefinition)).build();
        final Type join = repository.executeWithThrowing(TypeCreateCommand.of(typeDraft))
                .toCompletableFuture().join();
        System.out.println("Create type");
    }

    public CompletionStage<ShoppingList> updateWishList(ShoppingList shoppingList, UpdateAction<ShoppingList> action) {
        return repository.executeWithThrowing(ShoppingListUpdateCommand.of(shoppingList, action)
                .plusExpansionPaths(ShoppingListExpansionModel::customer)
                .plusExpansionPaths(ShoppingListExpansionModel::lineItems));
    }
}
