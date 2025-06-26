package com.stefan.ecommerce.repositories;

import com.stefan.ecommerce.entities.Wishlist;
import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    /**
     * Find wishlist by user ID
     */
    List<Wishlist> findByUserId(Long userId);

    /**
     * Find all wishlist items for a user
     */
    List<Wishlist> findByUser(User user);

    /**
     * Find wishlist item by user and product
     */
    Optional<Wishlist> findByUserAndProduct(User user, Product product);

    /**
     * Check if a product exists in user's wishlist
     */
    boolean existsByUserAndProduct(User user, Product product);

    /**
     * Check if a product exists in user's wishlist
     */
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wishlist w WHERE w.user.id = :userId AND w.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * Count products in user's wishlist
     */
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.user.id = :userId")
    int countProductsByUserId(@Param("userId") Long userId);

    /**
     * Count wishlist items for user
     */
    long countByUser(User user);

    /**
     * Delete wishlist by user ID
     */
    void deleteByUserId(Long userId);
} 