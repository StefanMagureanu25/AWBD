package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.Wishlist;
import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.services.WishlistService;
import com.stefan.ecommerce.services.UserService;
import com.stefan.ecommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String viewWishlist(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Wishlist> wishlistItems = wishlistService.getWishlistByUserId(user.getId());
            model.addAttribute("wishlistItems", wishlistItems);
        }
        
        return "wishlist/index";
    }

    @PostMapping("/add/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToWishlist(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        Optional<Product> productOpt = productService.findById(productId);
        
        if (userOpt.isPresent() && productOpt.isPresent()) {
            User user = userOpt.get();
            Product product = productOpt.get();
            
            Wishlist wishlistItem = wishlistService.addToWishlist(user, product);
            
            response.put("success", true);
            response.put("message", "Product added to wishlist successfully");
            response.put("wishlistItemId", wishlistItem.getId());
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Failed to add product to wishlist");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromWishlist(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        Optional<Product> productOpt = productService.findById(productId);
        
        if (userOpt.isPresent() && productOpt.isPresent()) {
            User user = userOpt.get();
            Product product = productOpt.get();
            
            wishlistService.removeFromWishlist(user, product);
            
            response.put("success", true);
            response.put("message", "Product removed from wishlist successfully");
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Failed to remove product from wishlist");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearWishlist() {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            wishlistService.clearWishlist(user.getId());
            
            response.put("success", true);
            response.put("message", "Wishlist cleared successfully");
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Failed to clear wishlist");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/check/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkWishlistStatus(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        Optional<Product> productOpt = productService.findById(productId);
        
        if (userOpt.isPresent() && productOpt.isPresent()) {
            User user = userOpt.get();
            Product product = productOpt.get();
            
            boolean isInWishlist = wishlistService.isProductInUserWishlist(user, product);
            
            response.put("success", true);
            response.put("inWishlist", isInWishlist);
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Failed to check wishlist status");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update-notes/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateNotes(@PathVariable Long productId, @RequestParam String notes) {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        Optional<Product> productOpt = productService.findById(productId);
        
        if (userOpt.isPresent() && productOpt.isPresent()) {
            User user = userOpt.get();
            Product product = productOpt.get();
            
            wishlistService.updateWishlistItemNotes(user, product, notes);
            
            response.put("success", true);
            response.put("message", "Notes updated successfully");
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Failed to update notes");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getWishlistCount() {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            long count = wishlistService.countWishlistItemsByUser(user);
            
            response.put("success", true);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Failed to get wishlist count");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 