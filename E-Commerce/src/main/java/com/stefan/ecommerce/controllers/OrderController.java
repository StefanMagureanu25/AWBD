package com.stefan.ecommerce.controllers;

import com.stefan.ecommerce.entities.Order;
import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.services.OrderService;
import com.stefan.ecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String listAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    @GetMapping
    public String listOrders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Order> orders = orderService.getOrdersByUser(user);
            model.addAttribute("orders", orders);
        }
        
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Optional<Order> orderOpt = orderService.getOrderById(id);
        
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            model.addAttribute("order", order);
            return "orders/details";
        }
        
        return "redirect:/orders";
    }

    @PostMapping("/admin/{id}/delete")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.deleteOrder(id);
        redirectAttributes.addFlashAttribute("successMessage", "Order deleted.");
        return "redirect:/orders/admin";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/confirm")
    public String confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/ship")
    public String shipOrder(@PathVariable Long id) {
        orderService.shipOrder(id);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/deliver")
    public String deliverOrder(@PathVariable Long id) {
        orderService.deliverOrder(id);
        return "redirect:/orders/" + id;
    }
} 