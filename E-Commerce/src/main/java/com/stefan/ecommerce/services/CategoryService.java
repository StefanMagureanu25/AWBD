package com.stefan.ecommerce.services;

import com.stefan.ecommerce.entities.Category;
import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.repositories.CategoryRepository;
import com.stefan.ecommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // ==================== CATEGORY CREATION & MANAGEMENT ====================

    /**
     * Create a new category
     */
    public Category createCategory(String name, String description, String imageUrl) {
        validateCategoryData(name);

        // Check if category name already exists
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category name already exists: " + name);
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setImageUrl(imageUrl);
        category.setActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    /**
     * Update existing category
     */
    public Category updateCategory(Long categoryId, String name, String description, String imageUrl) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        validateCategoryData(name);

        // Check if new name already exists (excluding current category)
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, categoryId)) {
            throw new IllegalArgumentException("Category name already exists: " + name);
        }

        category.setName(name);
        category.setDescription(description);
        category.setImageUrl(imageUrl);
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    /**
     * Validate category data
     */
    private void validateCategoryData(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
    }

    // ==================== CATEGORY RETRIEVAL ====================

    /**
     * Find category by ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Find category by ID with products
     */
    @Transactional(readOnly = true)
    public Optional<Category> findByIdWithProducts(Long id) {
        return categoryRepository.findByIdWithProducts(id);
    }

    /**
     * Find category by name (case-insensitive)
     */
    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }

    /**
     * Get all active categories
     */
    @Transactional(readOnly = true)
    public List<Category> findAllActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }

    /**
     * Get all active categories ordered by name
     */
    @Transactional(readOnly = true)
    public List<Category> findAllActiveCategoriesOrdered() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    /**
     * Get all active categories with pagination
     */
    @Transactional(readOnly = true)
    public Page<Category> findAllActiveCategories(Pageable pageable) {
        return categoryRepository.findByActiveTrue(pageable);
    }

    /**
     * Get all categories (admin view)
     */
    @Transactional(readOnly = true)
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Get all categories ordered by name (for admin dropdown)
     */
    @Transactional(readOnly = true)
    public List<Category> findAllCategoriesOrdered() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    // ==================== CATEGORY SEARCH & FILTERING ====================

    /**
     * Search categories by name
     */
    @Transactional(readOnly = true)
    public List<Category> searchByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }

    /**
     * Search categories by name with pagination
     */
    @Transactional(readOnly = true)
    public Page<Category> searchByName(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
    }

    /**
     * Complex search in name and description
     */
    @Transactional(readOnly = true)
    public List<Category> searchCategories(String searchTerm) {
        return categoryRepository.searchCategories(searchTerm);
    }

    /**
     * Search categories with pagination
     */
    @Transactional(readOnly = true)
    public Page<Category> searchCategories(String searchTerm, Pageable pageable) {
        return categoryRepository.searchCategories(searchTerm, pageable);
    }

    /**
     * Find categories by description
     */
    @Transactional(readOnly = true)
    public List<Category> searchByDescription(String description) {
        return categoryRepository.findByDescriptionContainingIgnoreCaseAndActiveTrue(description);
    }

    // ==================== CATEGORY-PRODUCT RELATIONSHIPS ====================

    /**
     * Get categories that have products
     */
    @Transactional(readOnly = true)
    public List<Category> findCategoriesWithProducts() {
        return categoryRepository.findCategoriesWithProducts();
    }

    /**
     * Get empty categories (without products)
     */
    @Transactional(readOnly = true)
    public List<Category> findEmptyCategories() {
        return categoryRepository.findEmptyCategories();
    }

    /**
     * Get active categories with their active products
     */
    @Transactional(readOnly = true)
    public List<Category> findActiveCategoriesWithActiveProducts() {
        return categoryRepository.findActiveCategoriesWithActiveProducts();
    }

    /**
     * Get categories with product count
     */
    @Transactional(readOnly = true)
    public List<Object[]> findCategoriesWithProductCount() {
        return categoryRepository.findCategoriesWithProductCount();
    }

    /**
     * Get categories ordered by product count (most popular first)
     */
    @Transactional(readOnly = true)
    public List<Category> findCategoriesOrderByProductCount() {
        return categoryRepository.findCategoriesOrderByProductCountDesc();
    }

    // ==================== CATEGORY STATUS MANAGEMENT ====================

    /**
     * Activate category
     */
    public void activateCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        category.setActive(true);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    /**
     * Deactivate category
     */
    public void deactivateCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        category.setActive(false);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    // ==================== PRODUCT MANAGEMENT ====================

    /**
     * Add product to category
     */
    public Category addProductToCategory(Long categoryId, Long productId) {
        Category category = categoryRepository.findByIdWithProducts(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        Product product = productRepository.findByIdWithCategories(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        category.addProduct(product);
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    /**
     * Remove product from category
     */
    public Category removeProductFromCategory(Long categoryId, Long productId) {
        Category category = categoryRepository.findByIdWithProducts(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        Product product = productRepository.findByIdWithCategories(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        category.removeProduct(product);
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    /**
     * Get products in category
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsInCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId);
    }

    /**
     * Get product count in category
     */
    @Transactional(readOnly = true)
    public long getProductCountInCategory(Long categoryId) {
        return productRepository.countByCategoryIdAndActiveTrue(categoryId);
    }

    // ==================== SORTING & PAGINATION ====================

    /**
     * Get recently created categories
     */
    @Transactional(readOnly = true)
    public List<Category> getRecentCategories(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return categoryRepository.findRecentCategories(pageable);
    }

    /**
     * Create pageable with sorting
     */
    public Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }

    // ==================== STATISTICS ====================

    /**
     * Get total category count
     */
    @Transactional(readOnly = true)
    public long getTotalCategoryCount() {
        return categoryRepository.count();
    }

    /**
     * Get active category count
     */
    @Transactional(readOnly = true)
    public long getActiveCategoryCount() {
        return categoryRepository.countByActiveTrue();
    }

    /**
     * Get inactive categories
     */
    @Transactional(readOnly = true)
    public List<Category> getInactiveCategories() {
        return categoryRepository.findByActiveFalse();
    }

    // ==================== VALIDATION METHODS ====================

    /**
     * Check if category name exists
     */
    @Transactional(readOnly = true)
    public boolean categoryNameExists(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    /**
     * Check if category name exists excluding current category (for updates)
     */
    @Transactional(readOnly = true)
    public boolean categoryNameExistsExcluding(String name, Long categoryId) {
        return categoryRepository.existsByNameIgnoreCaseAndIdNot(name, categoryId);
    }

    /**
     * Validate category name format
     */
    public boolean isValidCategoryName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }

    // ==================== BULK OPERATIONS ====================

    /**
     * Activate multiple categories
     */
    public void activateCategories(List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            activateCategory(categoryId);
        }
    }

    /**
     * Deactivate multiple categories
     */
    public void deactivateCategories(List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            deactivateCategory(categoryId);
        }
    }

    /**
     * Delete categories that have no products
     */
    public void deleteEmptyCategories() {
        List<Category> emptyCategories = findEmptyCategories();
        for (Category category : emptyCategories) {
            deactivateCategory(category.getId());
        }
    }

    // ==================== DELETE CATEGORY ====================

    /**
     * Delete category (soft delete by deactivating)
     */
    public void deleteCategory(Long categoryId) {
        // Check if category has products
        long productCount = getProductCountInCategory(categoryId);
        if (productCount > 0) {
            throw new IllegalArgumentException("Cannot delete category with products. " +
                    "Remove all products first or deactivate the category.");
        }

        deactivateCategory(categoryId);
    }

    /**
     * Force delete category (removes from all products first)
     */
    public void forceDeleteCategory(Long categoryId) {
        Category category = categoryRepository.findByIdWithProducts(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        // Remove category from all products
        for (Product product : category.getProducts()) {
            product.getCategories().remove(category);
            productRepository.save(product);
        }

        // Now delete the category
        deactivateCategory(categoryId);
    }

    /**
     * Permanently delete category (use with caution)
     */
    public void permanentlyDeleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found: " + categoryId);
        }

        // Check if category has products
        long productCount = getProductCountInCategory(categoryId);
        if (productCount > 0) {
            throw new IllegalArgumentException("Cannot permanently delete category with products. " +
                    "Remove all products first.");
        }

        categoryRepository.deleteById(categoryId);
    }
}