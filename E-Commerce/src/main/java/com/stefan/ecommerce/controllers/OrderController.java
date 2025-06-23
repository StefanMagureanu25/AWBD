package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.Order;
import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.services.OrderService;
import com.stefan.ecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // Admin: List all orders
    @GetMapping("/admin")
    public String listAllOrders(Model model) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    // User: List own orders
    @GetMapping
    public String listUserOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/users/login";
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) return "redirect:/users/login";
        List<Order> orders = user.getOrders().stream().toList();
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    // View order details (admin or owner)
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<Order> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) return "error/404";
        Order order = orderOpt.get();
        boolean isAdmin = userDetails != null && userService.isAdmin(userDetails.getUsername());
        boolean isOwner = userDetails != null && order.getUser().getUsername().equals(userDetails.getUsername());
        if (!isAdmin && !isOwner) return "error/access-denied";
        model.addAttribute("order", order);
        return "orders/details";
    }

    // Admin: Delete order
    @PostMapping("/admin/{id}/delete")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Order deleted.");
        return "redirect:/orders/admin";
    }
} 