package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.services.UserService;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "users/register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match");
            return "users/register";
        }

        if (user.getPassword().length() < 8) {
            model.addAttribute("errorMessage", "Password must be at least 8 characters long");
            return "users/register";
        }

        try {
            User createdUser = userService.registerUser(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName()
            );

            redirectAttributes.addFlashAttribute("successMessage",
                    "User '" + createdUser.getUsername() + "' registered successfully! Please log in.");
            return "redirect:/users/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully");
        }
        return "users/login";
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        return "users/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") User user,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "users/profile";
        }

        try {
            User updatedUser = userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/users/profile";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/profile";
        }
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm(Model model) {
        return "users/change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "New passwords do not match");
            return "users/change-password";
        }

        if (newPassword.length() < 8) {
            model.addAttribute("errorMessage", "Password must be at least 8 characters long");
            return "users/change-password";
        }

        try {
            Optional<User> currentUser = userService.getCurrentUser();
            if (currentUser.isPresent()) {
                userService.changePassword(currentUser.get().getId(), currentPassword, newPassword);
                redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
                return "redirect:/users/profile";
            } else {
                model.addAttribute("errorMessage", "User not found");
                return "users/change-password";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/change-password";
        }
    }

    @GetMapping("/admin/list")
    public String showAdminUserList(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @GetMapping("/admin/search")
    public String searchUsers(@RequestParam("q") String searchTerm, Model model) {
        List<User> users = userService.searchUsersByName(searchTerm, searchTerm);
        model.addAttribute("users", users);
        model.addAttribute("searchTerm", searchTerm);
        return "admin/users/search-results";
    }

    @PostMapping("/admin/{id}/activate")
    public String activateUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.enableUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User activated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/admin/list";
    }

    @PostMapping("/admin/{id}/deactivate")
    public String deactivateUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.disableUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deactivated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/admin/list";
    }

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

    @GetMapping("/api/users")
    @ResponseBody
    public List<User> getAllUsersApi() {
        return userService.findAllUsers();
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public User getUserByIdApi(@PathVariable("id") Long id) {
        return userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PostMapping("/api/users")
    @ResponseBody
    public User createUserApi(@RequestBody User user) {
        return userService.registerUser(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public User updateUserApi(@PathVariable("id") Long id, @RequestBody User user) {
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        return userService.updateUser(existingUser);
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public String deleteUserApi(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }

    @PostMapping("/admin/{id}/reset-password")
    public String resetUserPassword(@PathVariable("id") Long id,
                                    @RequestParam("newPassword") String newPassword,
                                    @RequestParam("confirmPassword") String confirmPassword,
                                    RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match");
            return "redirect:/users/admin/list";
        }

        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 8 characters long");
            return "redirect:/users/admin/list";
        }

        try {
            userService.resetPassword(id, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/admin/list";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/400";
    }
}