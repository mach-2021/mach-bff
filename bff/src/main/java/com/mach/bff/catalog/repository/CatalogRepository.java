package com.mach.bff.catalog.repository;

import com.mach.commercetools.repository.BaseRepository;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.expansion.CategoryExpansionModel;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletionStage;

@Repository
@RequiredArgsConstructor
public class CatalogRepository {
    private final BaseRepository repository;

    public CompletionStage<PagedQueryResult<Category>> getCategories() {
        return repository.executeWithThrowing(CategoryQuery.of()
                .plusExpansionPaths(CategoryExpansionModel::parent)
                .plusExpansionPaths(CategoryExpansionModel::ancestors)
                .withLimit(500));
    }

    public CompletionStage<PagedQueryResult<Category>> getRootCategories() {
        return repository.executeWithThrowing(CategoryQuery.of().byIsRoot()
                .plusExpansionPaths(CategoryExpansionModel::parent)
                .plusExpansionPaths(CategoryExpansionModel::ancestors)
                .withLimit(500));
    }
}
