package com.mach.bff.catalog.service;

import com.mach.bff.catalog.mapper.CatalogMapper;
import com.mach.bff.catalog.repository.CatalogRepository;
import com.mach.core.model.catalog.CategoryModel;
import com.mach.core.model.catalog.CategoryTreeModel;
import com.mach.core.model.catalog.response.CategoryResponse;
import io.sphere.sdk.categories.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {
    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;

    public CategoryResponse<CategoryTreeModel> getCategoriesTree() {
        List<Category> categories = catalogRepository.getCategories()
                .toCompletableFuture().join().getResults();
        if (CollectionUtils.isEmpty(categories)) {
            log.error("Categories do not exist in project");
            return new CategoryResponse<>(List.of());
        }
        log.info("Find categories {}", categories.size());
        return catalogMapper.mapCategoriesToTreeResponse(categories);
    }

    public CategoryResponse<CategoryModel> getCategories() {
        List<Category> categories = catalogRepository.getCategories()
                .toCompletableFuture().join().getResults();
        if (CollectionUtils.isEmpty(categories)) {
            log.error("Categories do not exist in project");
            return new CategoryResponse<>(List.of());
        }
        log.info("Find categories {}", categories.size());
        return catalogMapper.mapCategoriesToCategoryResponse(categories);
    }
}
