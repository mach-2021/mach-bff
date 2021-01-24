package com.mach.bff.catalog.mapper;

import com.mach.core.model.catalog.CategoryModel;
import com.mach.core.model.catalog.CategoryTreeModel;
import com.mach.core.model.catalog.response.CategoryResponse;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.CategoryTree;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class CatalogMapper {

    public CategoryResponse<CategoryTreeModel> mapCategoriesToTreeResponse(List<Category> categories) {
        final CategoryTree tree = CategoryTree.of(ListUtils.emptyIfNull(categories));
        final List<CategoryTreeModel> categoryTreeModels = tree.getRoots().stream()
                .map(cat -> mapItem(cat, tree))
                .collect(Collectors.toList());
        return new CategoryResponse<>(categoryTreeModels);
    }

    public CategoryResponse<CategoryModel> mapCategoriesToCategoryResponse(List<Category> categories) {
        final List<CategoryModel> categoryModels = categories.stream()
                .map(cat -> CategoryModel.builder()
                        .id(cat.getId())
                        .name(cat.getName().get(Locale.US))
                        .build())
                .collect(Collectors.toList());
        return new CategoryResponse<>(categoryModels);
    }

    private CategoryTreeModel mapItem(final Category cat, final CategoryTree tree) {
        final List<CategoryTreeModel> children = tree.findChildren(cat)
                .stream()
                .map(child -> mapItem(child, tree))
                .collect(Collectors.toList());

        return CategoryTreeModel.builder()
                .id(cat.getId())
                .name(cat.getName().get(Locale.US))
                .children(children)
                .build();
    }


}
