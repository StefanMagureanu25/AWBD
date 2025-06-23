package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.entities.Category;
import com.stefan.ecommerce.services.ProductService;
import com.stefan.ecommerce.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * Show product catalog with pagination
     */
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

        return "products/catalog";
    }

    /**
     * Show product by ID
     */
    @GetMapping("/{id}")
    public String showProduct(@PathVariable("id") Long id, Model model) {
        Optional<Product> productOpt = productService.findByIdWithCategories(id);

        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            return "products/details";
        } else {
            return "error/404";
        }
    }

    /**
     * Show products by category
     */
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

            return "products/category";
        } else {
            return "error/404";
        }
    }

    /**
     * Search products
     */
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

    // ==================== ADMIN PRODUCT MANAGEMENT ====================

    /**
     * Show product creation form
     */
    @GetMapping("/admin/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAllActiveCategories());
        return "admin/products/create";
    }

    /**
     * Process product creation
     */
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
            // Prepare categories set
            Set<Category> categories = new HashSet<>();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                for (Long categoryId : categoryIds) {
                    Optional<Category> categoryOpt = categoryService.findById(categoryId);
                    categoryOpt.ifPresent(categories::add);
                }
            }

            // Create the product with correct parameter order
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

            // Set active status if needed
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

    /**
     * Show all products for admin management
     */
    @GetMapping("/admin/list")
    public String showAdminProductList(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Use findAllProducts method that returns all products including inactive ones
        List<Product> allProducts = productService.findAllProducts();

        // For pagination, we can create a simple Page implementation or modify the service
        // For now, let's use findAllActiveProducts and note this needs proper implementation
        Page<Product> productsPage = productService.findAllActiveProducts(pageable);

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);

        return "admin/products/list";
    }

    /**
     * Show edit product form
     */
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

    /**
     * Process product update
     */
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
            // Prepare categories set
            Set<Category> categories = new HashSet<>();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                for (Long categoryId : categoryIds) {
                    Optional<Category> categoryOpt = categoryService.findById(categoryId);
                    categoryOpt.ifPresent(categories::add);
                }
            }

            // Update the product with correct parameter order
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

            // Handle active status
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

    /**
     * Delete product
     */
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

    /**
     * Activate product
     */
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

    /**
     * Deactivate product
     */
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

    // ==================== QUICK ACCESS ROUTES ====================

    /**
     * Featured products
     */
    @GetMapping("/featured")
    public String showFeaturedProducts(Model model) {
        List<Product> featuredProducts = productService.getFeaturedProducts(20);
        model.addAttribute("products", featuredProducts);
        model.addAttribute("pageTitle", "Featured Products");
        return "products/featured";
    }

    /**
     * Best deals (cheapest products)
     */
    @GetMapping("/cheapest")
    public String showBestDeals(Model model) {
        List<Product> cheapProducts = productService.getProductsSortedByPriceAsc(20);
        model.addAttribute("products", cheapProducts);
        model.addAttribute("pageTitle", "Best Deals");
        return "products/deals";
    }

    /**
     * Premium products (most expensive)
     */
    @GetMapping("/premium")
    public String showPremiumProducts(Model model) {
        List<Product> expensiveProducts = productService.getProductsSortedByPriceDesc(20);
        model.addAttribute("products", expensiveProducts);
        model.addAttribute("pageTitle", "Premium Collection");
        return "products/premium";
    }
}