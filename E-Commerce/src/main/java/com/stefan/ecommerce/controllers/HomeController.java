package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.entities.Category;
import com.stefan.ecommerce.services.ProductService;
import com.stefan.ecommerce.services.CategoryService;
import com.stefan.ecommerce.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public HomeController(ProductService productService, CategoryService categoryService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    /**
     * Main landing page
     */
    @GetMapping("/")
    public String home(Model model) {
        logger.info("HomeController: home() called");
        // Featured products (recently added)
        List<Product> featuredProducts;
        try {
            featuredProducts = productService.getFeaturedProducts(8);
        } catch (Exception e) {
            featuredProducts = java.util.Collections.emptyList();
        }
        model.addAttribute("featuredProducts", featuredProducts != null ? featuredProducts : java.util.Collections.emptyList());

        // Site statistics for homepage banners
        long totalProducts = 0;
        long totalCategories = 0;
        long totalUsers = 0;
        try {
            totalProducts = productService.getActiveProductCount();
        } catch (Exception ignored) {}
        try {
            totalCategories = categoryService.getActiveCategoryCount();
        } catch (Exception ignored) {}
        try {
            totalUsers = userService.getEnabledUserCount();
        } catch (Exception ignored) {}

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalUsers", totalUsers);

        return "index";
    }

    /**
     * Global search across products and categories
     */
    @GetMapping("/search")
    public String globalSearch(@RequestParam("q") String searchTerm, Model model) {
        // Search products
        List<Product> products = productService.searchProducts(searchTerm);
        if (products.size() > 10) {
            products = products.subList(0, 10); // Limit to 10 for overview
        }

        // Search categories
        List<Category> categories = categoryService.searchCategories(searchTerm);
        if (categories.size() > 5) {
            categories = categories.subList(0, 5); // Limit to 5 for overview
        }

        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalProductResults", productService.searchProducts(searchTerm).size());
        model.addAttribute("totalCategoryResults", categoryService.searchCategories(searchTerm).size());

        return "search/global-results";
    }

    /**
     * Admin dashboard
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        // Site statistics
        long totalProducts = productService.getTotalProductCount();
        long activeProducts = productService.getActiveProductCount();
        long totalCategories = categoryService.getTotalCategoryCount();
        long activeCategories = categoryService.getActiveCategoryCount();
        long totalUsers = userService.getTotalUserCount();
        long enabledUsers = userService.getEnabledUserCount();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("inactiveProducts", totalProducts - activeProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("activeCategories", activeCategories);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("enabledUsers", enabledUsers);
        model.addAttribute("disabledUsers", totalUsers - enabledUsers);

        // Recent activity
        List<Product> recentProducts = productService.getFeaturedProducts(5);
        model.addAttribute("recentProducts", recentProducts);

        List<Category> recentCategories = categoryService.getRecentCategories(5);
        model.addAttribute("recentCategories", recentCategories);

        // Alerts and warnings
        List<Product> lowStockProducts = productService.getLowStockProducts(10);
        List<Product> outOfStockProducts = productService.getOutOfStockProducts();
        List<Category> emptyCategories = categoryService.findEmptyCategories();

        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("outOfStockProducts", outOfStockProducts);
        model.addAttribute("emptyCategories", emptyCategories);
        model.addAttribute("lowStockCount", lowStockProducts.size());
        model.addAttribute("outOfStockCount", outOfStockProducts.size());
        model.addAttribute("emptyCategoriesCount", emptyCategories.size());

        return "admin/dashboard";
    }

    /**
     * About us page
     */
    @GetMapping("/about")
    public String about(Model model) {
        // Site statistics for about page
        model.addAttribute("totalProducts", productService.getActiveProductCount());
        model.addAttribute("totalCategories", categoryService.getActiveCategoryCount());
        model.addAttribute("totalCustomers", userService.getUsersWithOrders().size());

        return "info/about";
    }

    /**
     * Access denied page
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}