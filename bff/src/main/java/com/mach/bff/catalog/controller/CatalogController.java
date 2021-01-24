package com.mach.bff.catalog.controller;

import com.mach.bff.catalog.service.CatalogService;
import com.mach.core.model.catalog.CategoryModel;
import com.mach.core.model.catalog.CategoryTreeModel;
import com.mach.core.model.catalog.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;

    @GetMapping("/categories/tree")
    public ResponseEntity<CategoryResponse<CategoryTreeModel>> getCategoriesTree() {
        return ResponseEntity.ok(catalogService.getCategoriesTree());
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoryResponse<CategoryModel>> getCategories() {
        return ResponseEntity.ok(catalogService.getCategories());
    }
}
