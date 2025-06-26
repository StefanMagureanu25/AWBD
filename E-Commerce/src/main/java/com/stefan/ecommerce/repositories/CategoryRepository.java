package com.stefan.ecommerce.repositories;

import com.stefan.ecommerce.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByActiveTrue();

    List<Category> findByActiveTrueOrderByNameAsc();

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    List<Category> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products p WHERE c.active = true AND (p.active = true OR p IS NULL)")
    List<Category> findActiveCategoriesWithActiveProducts();

    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE c.active = true AND p.active = true")
    List<Category> findCategoriesWithProducts();

    @Query("SELECT c FROM Category c WHERE c.active = true AND c.products IS EMPTY")
    List<Category> findEmptyCategories();

    @Query("SELECT c.id, c.name, COUNT(p) FROM Category c LEFT JOIN c.products p WHERE c.active = true AND (p.active = true OR p IS NULL) GROUP BY c.id, c.name")
    List<Object[]> findCategoriesWithProductCount();

    @Query("SELECT c FROM Category c LEFT JOIN c.products p WHERE c.active = true AND (p.active = true OR p IS NULL) GROUP BY c ORDER BY COUNT(p) DESC")
    List<Category> findCategoriesOrderByProductCountDesc();

    Page<Category> findByActiveTrue(Pageable pageable);

    Page<Category> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    long countByActiveTrue();

    List<Category> findByActiveFalse();

    // Find all categories ordered by name (for admin dropdown)
    List<Category> findAllByOrderByNameAsc();

    // Find all categories with their products (for admin)
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products")
    List<Category> findAllWithProducts();

    // Find recently created categories
    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.createdAt DESC")
    List<Category> findRecentCategories(Pageable pageable);

    // Find categories by description containing text
    List<Category> findByDescriptionContainingIgnoreCaseAndActiveTrue(String description);

    // Complex search in name and description
    @Query("SELECT c FROM Category c WHERE c.active = true AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Category> searchCategories(@Param("searchTerm") String searchTerm);

    // Search categories with pagination
    @Query("SELECT c FROM Category c WHERE c.active = true AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Category> searchCategories(@Param("searchTerm") String searchTerm, Pageable pageable);
}