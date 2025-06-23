package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==================== USER REGISTRATION ====================

    /**
     * Show registration form
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    /**
     * Process user registration
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "users/register";
        }

        // Validate password confirmation
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("passwordError", "Passwords do not match");
            return "users/register";
        }

        // Validate password strength
        if (!userService.isValidPassword(user.getPassword())) {
            model.addAttribute("passwordError", "Password must be at least 8 characters long");
            return "users/register";
        }

        try {
            userService.registerUser(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName()
            );

            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! You can now log in.");
            return "redirect:/users/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/register";
        }
    }

    // ==================== USER LOGIN ====================

    /**
     * Show login form
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "users/login";
    }

    /**
     * Custom login processing (if not using Spring Security default)
     */
    @PostMapping("/login")
    public String processLogin(@RequestParam("login") String login,
                               @RequestParam("password") String password,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (userService.validateCredentials(login, password)) {
            Optional<User> userOpt = userService.findByEmailOrUsername(login);
            if (userOpt.isPresent()) {
                // Set user in session (you'd normally use Spring Security for this)
                redirectAttributes.addFlashAttribute("successMessage",
                        "Welcome back, " + userOpt.get().getFirstName() + "!");
                return "redirect:/";
            }
        }

        model.addAttribute("errorMessage", "Invalid username/email or password");
        return "users/login";
    }

    // ==================== USER PROFILE ====================

    /**
     * Show user profile
     */
    @GetMapping("/profile/{id}")
    public String showUserProfile(@PathVariable("id") Long id, Model model) {
        Optional<User> userOpt = userService.findByIdWithProfile(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "users/profile";
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "error/404";
        }
    }

    /**
     * Show edit profile form
     */
    @GetMapping("/profile/{id}/edit")
    public String showEditProfileForm(@PathVariable("id") Long id, Model model) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "users/edit-profile";
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "error/404";
        }
    }

    /**
     * Update user profile
     */
    @PostMapping("/profile/{id}/edit")
    public String updateUserProfile(@PathVariable("id") Long id,
                                    @RequestParam("firstName") String firstName,
                                    @RequestParam("lastName") String lastName,
                                    @RequestParam("email") String email,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserInfo(id, firstName, lastName, email);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/users/profile/" + id;

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                model.addAttribute("user", userOpt.get());
            }
            return "users/edit-profile";
        }
    }

    // ==================== PASSWORD MANAGEMENT ====================

    /**
     * Show change password form
     */
    @GetMapping("/profile/{id}/change-password")
    public String showChangePasswordForm(@PathVariable("id") Long id, Model model) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("userId", id);
            return "users/change-password";
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "error/404";
        }
    }

    /**
     * Process password change
     */
    @PostMapping("/profile/{id}/change-password")
    public String changePassword(@PathVariable("id") Long id,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        // Validate new password confirmation
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "New passwords do not match");
            model.addAttribute("userId", id);
            return "users/change-password";
        }

        // Validate password strength
        if (!userService.isValidPassword(newPassword)) {
            model.addAttribute("errorMessage", "Password must be at least 8 characters long");
            model.addAttribute("userId", id);
            return "users/change-password";
        }

        try {
            userService.changePassword(id, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
            return "redirect:/users/profile/" + id;

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userId", id);
            return "users/change-password";
        }
    }

    // ==================== ADMIN USER MANAGEMENT ====================

    /**
     * Show all users (admin only)
     */
    @GetMapping("/admin/list")
    public String listAllUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", userService.getTotalUserCount());
        model.addAttribute("enabledUsers", userService.getEnabledUserCount());
        return "admin/users/list";
    }

    /**
     * Show enabled users only
     */
    @GetMapping("/admin/enabled")
    public String listEnabledUsers(Model model) {
        List<User> users = userService.findEnabledUsers();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Enabled Users");
        return "admin/users/list";
    }

    /**
     * Show disabled users only
     */
    @GetMapping("/admin/disabled")
    public String listDisabledUsers(Model model) {
        List<User> users = userService.findDisabledUsers();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Disabled Users");
        return "admin/users/list";
    }

    /**
     * Show users with orders
     */
    @GetMapping("/admin/customers")
    public String listCustomers(Model model) {
        List<User> users = userService.getUsersWithOrders();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Customers (Users with Orders)");
        return "admin/users/list";
    }

    // ==================== USER SEARCH ====================

    /**
     * Search users (admin)
     */
    @GetMapping("/admin/search")
    public String searchUsers(@RequestParam(value = "firstName", required = false) String firstName,
                              @RequestParam(value = "lastName", required = false) String lastName,
                              @RequestParam(value = "email", required = false) String email,
                              Model model) {

        List<User> users;
        String searchType = "";

        if (email != null && !email.trim().isEmpty()) {
            users = userService.searchUsersByEmail(email);
            searchType = "email: " + email;
        } else if (firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty()) {
            users = userService.searchUsersByName(firstName, lastName);
            searchType = "name: " + firstName + " " + lastName;
        } else {
            users = userService.findAllUsers();
            searchType = "all users";
        }

        model.addAttribute("users", users);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchResults", true);
        return "admin/users/search";
    }

    // ==================== USER STATUS MANAGEMENT ====================

    /**
     * Enable user account
     */
    @PostMapping("/admin/{id}/enable")
    public String enableUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.enableUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User enabled successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/admin/list";
    }

    /**
     * Disable user account
     */
    @PostMapping("/admin/{id}/disable")
    public String disableUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.disableUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User disabled successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/admin/list";
    }

    /**
     * Delete user account (soft delete)
     */
    @PostMapping("/admin/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/admin/list";
    }

    // ==================== REST API ENDPOINTS ====================

    /**
     * REST API: Get user by ID
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * REST API: Check if username exists
     */
    @GetMapping("/api/check-username")
    @ResponseBody
    public ResponseEntity<Boolean> checkUsername(@RequestParam("username") String username) {
        boolean exists = userService.usernameExists(username);
        return ResponseEntity.ok(exists);
    }

    /**
     * REST API: Check if email exists
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * REST API: Get all users
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * REST API: User registration
     */
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<String> registerUserAPI(@RequestBody User user) {
        try {
            userService.registerUser(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName()
            );
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== ADMIN PASSWORD RESET ====================

    /**
     * Show admin password reset form
     */
    @GetMapping("/admin/{id}/reset-password")
    public String showResetPasswordForm(@PathVariable("id") Long id, Model model) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "admin/users/reset-password";
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "error/404";
        }
    }

    /**
     * Process admin password reset
     */
    @PostMapping("/admin/{id}/reset-password")
    public String resetUserPassword(@PathVariable("id") Long id,
                                    @RequestParam("newPassword") String newPassword,
                                    @RequestParam("confirmPassword") String confirmPassword,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match");
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                model.addAttribute("user", userOpt.get());
            }
            return "admin/users/reset-password";
        }

        // Validate password strength
        if (!userService.isValidPassword(newPassword)) {
            model.addAttribute("errorMessage", "Password must be at least 8 characters long");
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                model.addAttribute("user", userOpt.get());
            }
            return "admin/users/reset-password";
        }

        try {
            userService.resetPassword(id, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully!");
            return "redirect:/users/admin/list";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                model.addAttribute("user", userOpt.get());
            }
            return "admin/users/reset-password";
        }
    }

    // ==================== ERROR HANDLING ====================

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        return "error/general";
    }
}