package com.stefan.ecommerce.services;

import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.entities.Category;
import com.stefan.ecommerce.repositories.ProductRepository;
import com.stefan.ecommerce.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // ==================== PRODUCT CREATION & MANAGEMENT ====================

    /**
     * Create a new product
     */
    public Product createProduct(String name, String description, BigDecimal price,
                                 Integer stockQuantity, String dimensions, Double weightKg,
                                 String imageUrl, Set<Category> categories) {

        validateProductData(name, price, stockQuantity);

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setDimensions(dimensions);
        product.setWeightKg(weightKg);
        product.setImageUrl(imageUrl);
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        if (categories != null && !categories.isEmpty()) {
            product.setCategories(categories);
        }

        return productRepository.save(product);
    }

    /**
     * Update existing product
     */
    public Product updateProduct(Long productId, String name, String description,
                                 BigDecimal price, Integer stockQuantity, String dimensions,
                                 Double weightKg, String imageUrl, Set<Category> categories) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        validateProductData(name, price, stockQuantity);

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setDimensions(dimensions);
        product.setWeightKg(weightKg);
        product.setImageUrl(imageUrl);
        product.setUpdatedAt(LocalDateTime.now());

        if (categories != null) {
            product.setCategories(categories);
        }

        return productRepository.save(product);
    }

    /**
     * Validate product data
     */
    private void validateProductData(String name, BigDecimal price, Integer stockQuantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    // ==================== PRODUCT RETRIEVAL ====================

    /**
     * Find product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Find product by ID with categories
     */
    @Transactional(readOnly = true)
    public Optional<Product> findByIdWithCategories(Long id) {
        return productRepository.findByIdWithCategories(id);
    }

    /**
     * Get all active products
     */
    @Transactional(readOnly = true)
    public List<Product> findAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    /**
     * Get all active products with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> findAllActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    /**
     * Get all products (admin view)
     */
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get all products with pagination (admin view)
     */
    @Transactional(readOnly = true)
    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Get featured products (recently added)
     */
    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findFeaturedProducts(pageable);
    }

    // ==================== PRODUCT SEARCH & FILTERING ====================

    /**
     * Search products by name
     */
    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }

    /**
     * Complex search in name, description, and categories
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchProducts(searchTerm);
    }

    /**
     * Search products with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.searchProducts(searchTerm, pageable);
    }

    /**
     * Search products with pagination (alternative method name for compatibility)
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProductsPaginated(String searchTerm, Pageable pageable) {
        return searchProducts(searchTerm, pageable);
    }

    /**
     * Find products by price range
     */
    @Transactional(readOnly = true)
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice);
    }

    /**
     * Find products by price range with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice, pageable);
    }

    /**
     * Find products by category
     */
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategoryAndActiveTrue(category);
    }

    /**
     * Find products by category with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> findByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategoryAndActiveTrue(category, pageable);
    }

    /**
     * Find products by category ID
     */
    @Transactional(readOnly = true)
    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId);
    }

    /**
     * Find products by category ID with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
        // This method assumes the repository has this method, or we can implement it differently
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            return productRepository.findByCategoryAndActiveTrue(category.get(), pageable);
        } else {
            // Return empty page if category not found
            return Page.empty(pageable);
        }
    }

    /**
     * Find products by category (alternative method for backward compatibility)
     */
    @Transactional(readOnly = true)
    public Page<Product> findByCategory(Long categoryId, Pageable pageable) {
        return findByCategoryId(categoryId, pageable);
    }

    // ==================== STOCK MANAGEMENT ====================

    /**
     * Update product stock
     */
    public Product updateStock(Long productId, Integer newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * Decrease stock (for order processing)
     */
    public Product decreaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " +
                    product.getStockQuantity() + ", Requested: " + quantity);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * Increase stock (for returns/restocking)
     */
    public Product increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        product.setStockQuantity(product.getStockQuantity() + quantity);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * Check if product is in stock
     */
    @Transactional(readOnly = true)
    public boolean isInStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        return product.getStockQuantity() >= quantity;
    }

    /**
     * Get low stock products
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThanAndActiveTrue(threshold);
    }

    /**
     * Get out of stock products
     */
    @Transactional(readOnly = true)
    public List<Product> getOutOfStockProducts() {
        return productRepository.findByStockQuantityAndActiveTrue(0);
    }

    // ==================== PRODUCT STATUS MANAGEMENT ====================

    /**
     * Activate product
     */
    public void activateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        product.setActive(true);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    /**
     * Deactivate product
     */
    public void deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    // ==================== CATEGORY MANAGEMENT ====================

    /**
     * Add category to product
     */
    public Product addCategoryToProduct(Long productId, Long categoryId) {
        Product product = productRepository.findByIdWithCategories(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        product.getCategories().add(category);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * Add product to category (alternative method name for backward compatibility)
     */
    public Product addProductToCategory(Long productId, Long categoryId) {
        return addCategoryToProduct(productId, categoryId);
    }

    /**
     * Remove category from product
     */
    public Product removeCategoryFromProduct(Long productId, Long categoryId) {
        Product product = productRepository.findByIdWithCategories(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        product.getCategories().remove(category);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * Update product categories
     */
    public Product updateProductCategories(Long productId, List<Long> categoryIds) {
        Product product = productRepository.findByIdWithCategories(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Clear existing categories
        product.getCategories().clear();

        // Add new categories
        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
                product.getCategories().add(category);
            }
        }

        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    // ==================== SORTING & PAGINATION ====================

    /**
     * Get products sorted by price (ascending)
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsSortedByPriceAsc(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findCheapestProducts(pageable);
    }

    /**
     * Get products sorted by price (descending)
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsSortedByPriceDesc(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findMostExpensiveProducts(pageable);
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
     * Get total product count
     */
    @Transactional(readOnly = true)
    public long getTotalProductCount() {
        return productRepository.count();
    }

    /**
     * Get active product count
     */
    @Transactional(readOnly = true)
    public long getActiveProductCount() {
        return productRepository.countByActiveTrue();
    }

    /**
     * Get product count by category
     */
    @Transactional(readOnly = true)
    public long getProductCountByCategory(Long categoryId) {
        return productRepository.countByCategoryIdAndActiveTrue(categoryId);
    }

    // ==================== DELETE PRODUCT ====================

    /**
     * Delete product (soft delete by deactivating)
     */
    public void deleteProduct(Long productId) {
        deactivateProduct(productId);
    }

    /**
     * Permanently delete product (use with caution)
     */
    public void permanentlyDeleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        productRepository.deleteById(productId);
    }
}