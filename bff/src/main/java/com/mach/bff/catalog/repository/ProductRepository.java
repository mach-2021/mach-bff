package com.mach.bff.catalog.repository;

import com.mach.commercetools.repository.BaseRepository;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.expansion.CategoryExpansionModel;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletionStage;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final BaseRepository repository;

    public CompletionStage<PagedQueryResult<ProductProjection>> findProductBySku(String sku) {
        return repository.executeWithThrowing(ProductProjectionQuery.ofCurrent()
                .bySku(sku)
                .plusExpansionPaths(ex -> ex.allVariants().attributes().value())
                .withLimit(1));
    }
}
