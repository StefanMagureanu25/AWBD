package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.Category;
import com.stefan.ecommerce.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Show all categories
     */
    @GetMapping("/categories")
    public String showAllCategories(Model model) {
        List<Category> categories = categoryService.findAllActiveCategories();
        model.addAttribute("categories", categories);
        return "categories/browse";
    }

    /**
     * Show create category form
     */
    @GetMapping("/admin/categories/create")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/create";
    }

    /**
     * Process category creation
     */
    @PostMapping("/admin/categories/create")
    public String createCategory(@Valid @ModelAttribute("category") Category category,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/categories/create";
        }

        try {
            Category createdCategory = categoryService.createCategory(
                    category.getName(),
                    category.getDescription(),
                    category.getImageUrl()
            );

            redirectAttributes.addFlashAttribute("successMessage",
                    "Category '" + createdCategory.getName() + "' created successfully!");
            return "redirect:/categories";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/categories/create";
        }
    }

    /**
     * Show edit category form
     */
    @GetMapping("/admin/categories/{id}/edit")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        Optional<Category> categoryOpt = categoryService.findById(id);

        if (categoryOpt.isPresent()) {
            model.addAttribute("category", categoryOpt.get());
            return "admin/categories/edit";
        } else {
            return "error/404";
        }
    }

    /**
     * Process category update
     */
    @PostMapping("/admin/categories/{id}/edit")
    public String updateCategory(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("category") Category category,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/categories/edit";
        }

        try {
            Category updatedCategory = categoryService.updateCategory(
                    id,
                    category.getName(),
                    category.getDescription(),
                    category.getImageUrl()
            );

            redirectAttributes.addFlashAttribute("successMessage",
                    "Category '" + updatedCategory.getName() + "' updated successfully!");
            return "redirect:/categories";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            Optional<Category> categoryOpt = categoryService.findById(id);
            if (categoryOpt.isPresent()) {
                model.addAttribute("category", categoryOpt.get());
            }
            return "admin/categories/edit";
        }
    }

    /**
     * Delete category
     */
    @PostMapping("/admin/categories/{id}/delete")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }

    /**
     * Activate category
     */
    @PostMapping("/admin/categories/{id}/activate")
    public String activateCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.activateCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category activated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }

    /**
     * Deactivate category
     */
    @PostMapping("/admin/categories/{id}/deactivate")
    public String deactivateCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deactivateCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deactivated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }
}