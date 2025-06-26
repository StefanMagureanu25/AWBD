package com.stefan.ecommerce.services;

import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.entities.Wishlist;
import com.stefan.ecommerce.repositories.ProductRepository;
import com.stefan.ecommerce.repositories.UserRepository;
import com.stefan.ecommerce.repositories.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository, 
                          UserRepository userRepository, 
                          ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Get all wishlist items for a user
     */
    public List<Wishlist> getWishlistByUser(User user) {
        return wishlistRepository.findByUser(user);
    }

    /**
     * Add product to user's wishlist
     */
    public Wishlist addToWishlist(User user, Product product) {
        // Check if already in wishlist
        Optional<Wishlist> existingWishlist = wishlistRepository.findByUserAndProduct(user, product);
        if (existingWishlist.isPresent()) {
            return existingWishlist.get();
        }

        Wishlist wishlistItem = new Wishlist(user, product);
        return wishlistRepository.save(wishlistItem);
    }

    /**
     * Remove product from user's wishlist
     */
    public void removeFromWishlist(User user, Product product) {
        Optional<Wishlist> wishlistItem = wishlistRepository.findByUserAndProduct(user, product);
        wishlistItem.ifPresent(wishlistRepository::delete);
    }

    /**
     * Clear user's wishlist
     */
    public void clearUserWishlist(User user) {
        List<Wishlist> userWishlist = wishlistRepository.findByUser(user);
        wishlistRepository.deleteAll(userWishlist);
    }

    /**
     * Check if product is in user's wishlist
     */
    public boolean isProductInUserWishlist(User user, Product product) {
        return wishlistRepository.existsByUserAndProduct(user, product);
    }

    /**
     * Update wishlist item notes
     */
    public void updateWishlistItemNotes(User user, Product product, String notes) {
        Optional<Wishlist> wishlistItem = wishlistRepository.findByUserAndProduct(user, product);
        if (wishlistItem.isPresent()) {
            Wishlist item = wishlistItem.get();
            item.setNotes(notes);
            wishlistRepository.save(item);
        }
    }

    /**
     * Count wishlist items for user
     */
    public long countWishlistItemsByUser(User user) {
        return wishlistRepository.countByUser(user);
    }

    /**
     * Get wishlist by user ID
     */
    public List<Wishlist> getWishlistByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    /**
     * Get all products in user's wishlist
     */
    public List<Product> getWishlistProducts(Long userId) {
        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(userId);
        return wishlistItems.stream()
                .map(Wishlist::getProduct)
                .toList();
    }

    /**
     * Check if product is in user's wishlist
     */
    public boolean isProductInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * Get wishlist product count for user
     */
    public int getWishlistCount(Long userId) {
        return wishlistRepository.countProductsByUserId(userId);
    }

    /**
     * Clear user's wishlist
     */
    public boolean clearWishlist(Long userId) {
        List<Wishlist> userWishlist = wishlistRepository.findByUserId(userId);
        if (userWishlist.isEmpty()) {
            return false;
        }
        wishlistRepository.deleteAll(userWishlist);
        return true;
    }

    /**
     * Delete user's wishlist
     */
    public boolean deleteWishlist(Long userId) {
        List<Wishlist> userWishlist = wishlistRepository.findByUserId(userId);
        if (userWishlist.isEmpty()) {
            return false;
        }
        wishlistRepository.deleteAll(userWishlist);
        return true;
    }
} 