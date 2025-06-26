package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.entities.Category;
import com.stefan.ecommerce.services.ProductService;
import com.stefan.ecommerce.services.CategoryService;
import com.stefan.ecommerce.services.WishlistService;
import com.stefan.ecommerce.services.UserService;
import com.stefan.ecommerce.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final WishlistService wishlistService;
    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, 
                           WishlistService wishlistService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    @GetMapping
    public String showProductCatalog(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "12") int size,
                                     @RequestParam(defaultValue = "name") String sortBy,
                                     @RequestParam(defaultValue = "asc") String sortDir,
                                     Model model) {

        Sort.Direction direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Product> productsPage = productService.findAllActiveProducts(pageable);
        List<Category> categories = categoryService.findAllActiveCategories();

        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("totalProducts", productsPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("wishlistService", wishlistService);
            
            Optional<User> userOpt = userService.findByUsername(authentication.getName());
            if (userOpt.isPresent()) {
                User currentUser = userOpt.get();
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("currentUserId", currentUser.getId());
            }
        }

        return "products/catalog";
    }

    @GetMapping("/{id}")
    public String showProduct(@PathVariable("id") Long id, Model model) {
        Optional<Product> productOpt = productService.findByIdWithCategories(id);

        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                model.addAttribute("wishlistService", wishlistService);
                
                Optional<User> userOpt = userService.findByUsername(authentication.getName());
                if (userOpt.isPresent()) {
                    User currentUser = userOpt.get();
                    model.addAttribute("currentUser", currentUser);
                    model.addAttribute("currentUserId", currentUser.getId());
                }
            }
            
            return "products/details";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/category/{categoryId}")
    public String showProductsByCategory(@PathVariable("categoryId") Long categoryId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "12") int size,
                                         Model model) {

        Optional<Category> categoryOpt = categoryService.findById(categoryId);

        if (categoryOpt.isPresent()) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productsPage = productService.findByCategoryId(categoryId, pageable);

            model.addAttribute("category", categoryOpt.get());
            model.addAttribute("productsPage", productsPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productsPage.getTotalPages());
            model.addAttribute("totalProducts", productsPage.getTotalElements());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                model.addAttribute("wishlistService", wishlistService);
                
                Optional<User> userOpt = userService.findByUsername(authentication.getName());
                if (userOpt.isPresent()) {
                    User currentUser = userOpt.get();
                    model.addAttribute("currentUser", currentUser);
                    model.addAttribute("currentUserId", currentUser.getId());
                }
            }

            return "products/category";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("q") String searchTerm,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "12") int size,
                                 Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productService.searchProducts(searchTerm, pageable);

        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("categories", categoryService.findAllActiveCategories());

        return "products/search-results";
    }

    @GetMapping("/admin/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAllActiveCategories());
        return "admin/products/create";
    }

    @PostMapping("/admin/create")
    public String createProduct(@Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllActiveCategories());
            return "admin/products/create";
        }

        try {
            Set<Category> categories = new HashSet<>();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                for (Long categoryId : categoryIds) {
                    Optional<Category> categoryOpt = categoryService.findById(categoryId);
                    categoryOpt.ifPresent(categories::add);
                }
            }

            Product createdProduct = productService.createProduct(
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.getDimensions(),
                    product.getWeightKg(),
                    product.getImageUrl(),
                    categories
            );

            if (!product.getActive()) {
                productService.deactivateProduct(createdProduct.getId());
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Product '" + createdProduct.getName() + "' created successfully!");
            return "redirect:/products";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating product: " + e.getMessage());
            model.addAttribute("categories", categoryService.findAllActiveCategories());
            return "admin/products/create";
        }
    }

    @GetMapping("/admin/list")
    public String showAdminProductList(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Product> allProducts = productService.findAllProducts();

        Page<Product> productsPage = productService.findAllActiveProducts(pageable);

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);

        return "admin/products/list";
    }

    @GetMapping("/admin/{id}/edit")
    public String showEditProductForm(@PathVariable("id") Long id, Model model) {
        Optional<Product> productOpt = productService.findByIdWithCategories(id);

        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            model.addAttribute("categories", categoryService.findAllActiveCategories());
            return "admin/products/edit";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/admin/{id}/edit")
    public String updateProduct(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllActiveCategories());
            return "admin/products/edit";
        }

        try {
            Set<Category> categories = new HashSet<>();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                for (Long categoryId : categoryIds) {
                    Optional<Category> categoryOpt = categoryService.findById(categoryId);
                    categoryOpt.ifPresent(categories::add);
                }
            }

            Product updatedProduct = productService.updateProduct(
                    id,
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.getDimensions(),
                    product.getWeightKg(),
                    product.getImageUrl(),
                    categories
            );

            if (product.getActive()) {
                productService.activateProduct(id);
            } else {
                productService.deactivateProduct(id);
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Product '" + updatedProduct.getName() + "' updated successfully!");
            return "redirect:/products/admin/list";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating product: " + e.getMessage());
            model.addAttribute("categories", categoryService.findAllActiveCategories());
            return "admin/products/edit";
        }
    }

    @PostMapping("/admin/{id}/delete")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products/admin/list";
    }

    @PostMapping("/admin/{id}/activate")
    public String activateProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.activateProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error activating product: " + e.getMessage());
        }
        return "redirect:/products/admin/list";
    }

    @PostMapping("/admin/{id}/deactivate")
    public String deactivateProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deactivateProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deactivating product: " + e.getMessage());
        }
        return "redirect:/products/admin/list";
    }

    @GetMapping("/featured")
    public String showFeaturedProducts(Model model) {
        List<Product> featuredProducts = productService.getFeaturedProducts(20);
        model.addAttribute("products", featuredProducts);
        model.addAttribute("title", "Featured Products");
        return "products/catalog";
    }

    @GetMapping("/cheapest")
    public String showBestDeals(Model model) {
        List<Product> cheapestProducts = productService.getProductsSortedByPriceAsc(20);
        model.addAttribute("products", cheapestProducts);
        model.addAttribute("title", "Best Deals");
        return "products/catalog";
    }

    @GetMapping("/premium")
    public String showPremiumProducts(Model model) {
        List<Product> premiumProducts = productService.getProductsSortedByPriceDesc(20);
        model.addAttribute("products", premiumProducts);
        model.addAttribute("title", "Premium Products");
        return "products/catalog";
    }
}