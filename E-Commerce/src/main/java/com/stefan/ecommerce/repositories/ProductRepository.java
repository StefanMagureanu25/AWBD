package com.stefan.ecommerce.repositories;

import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find all active products (for customer view)
    List<Product> findByActiveTrue();

    // Find all active products with pagination
    Page<Product> findByActiveTrue(Pageable pageable);

    // Find products by name (case-insensitive search)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find active products by name (customer search)
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    // Find products by price range
    List<Product> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    // Find products by category
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c = :category AND p.active = true")
    List<Product> findByCategoryAndActiveTrue(@Param("category") Category category);

    // Find products by category with pagination
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c = :category AND p.active = true")
    Page<Product> findByCategoryAndActiveTrue(@Param("category") Category category, Pageable pageable);

    // Find products by category ID
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId AND p.active = true")
    List<Product> findByCategoryIdAndActiveTrue(@Param("categoryId") Long categoryId);

    // Complex search query (name, description, category)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.categories c WHERE " +
            "p.active = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);

    // Search products with pagination
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.categories c WHERE " +
            "p.active = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find products with low stock (for admin alerts)
    List<Product> findByStockQuantityLessThanAndActiveTrue(Integer threshold);

    // Find out of stock products
    List<Product> findByStockQuantityAndActiveTrue(Integer stockQuantity);

    // Find products with categories loaded (performance optimization)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.categories WHERE p.id = :id")
    Optional<Product> findByIdWithCategories(@Param("id") Long id);

    // Find featured products (you can add a featured field later)
    // For now, let's get the most recently added products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.createdAt DESC")
    List<Product> findFeaturedProducts(Pageable pageable);

    // Find products by price range with pagination
    Page<Product> findByPriceBetweenAndActiveTrue(
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Count active products
    long countByActiveTrue();

    // Count products by category
    @Query("SELECT COUNT(p) FROM Product p JOIN p.categories c WHERE c.id = :categoryId AND p.active = true")
    long countByCategoryIdAndActiveTrue(@Param("categoryId") Long categoryId);

    // Find most expensive products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.price DESC")
    List<Product> findMostExpensiveProducts(Pageable pageable);

    // Find cheapest products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.price ASC")
    List<Product> findCheapestProducts(Pageable pageable);

    // Admin queries - include inactive products
    List<Product> findByActiveFalse();

    // Find all products with their categories (for admin)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.categories")
    List<Product> findAllWithCategories();
}