package com.stefan.ecommerce.services;

import com.stefan.ecommerce.entities.Order;
import com.stefan.ecommerce.entities.OrderItem;
import com.stefan.ecommerce.entities.Product;
import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.repositories.OrderRepository;
import com.stefan.ecommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetweenOrderByOrderDateDesc(startDate, endDate);
    }

    public List<Order> getOrdersByTotalAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return orderRepository.findByTotalAmountBetweenOrderByOrderDateDesc(minAmount, maxAmount);
    }

    public Order createOrder(User user, BigDecimal totalAmount) {
        String orderNumber = generateOrderNumber();
        Order order = new Order(orderNumber, user, totalAmount);
        return orderRepository.save(order);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

    public boolean existsByOrderNumber(String orderNumber) {
        return orderRepository.existsByOrderNumber(orderNumber);
    }

    public long countOrders() {
        return orderRepository.count();
    }

    public long countOrdersByUser(User user) {
        return orderRepository.countByUser(user);
    }

    public long countOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    public List<Order> getOrdersSortedByOrderDateDesc() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public List<Order> getOrdersSortedByOrderDateAsc() {
        return orderRepository.findAllByOrderByOrderDateAsc();
    }

    public List<Order> getOrdersSortedByTotalAmountDesc() {
        return orderRepository.findAllByOrderByTotalAmountDesc();
    }

    public List<Order> getOrdersSortedByTotalAmountAsc() {
        return orderRepository.findAllByOrderByTotalAmountAsc();
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByStatusOrderByOrderDateDesc(Order.OrderStatus.PENDING);
    }

    public List<Order> getConfirmedOrders() {
        return orderRepository.findByStatusOrderByOrderDateDesc(Order.OrderStatus.CONFIRMED);
    }

    public List<Order> getShippedOrders() {
        return orderRepository.findByStatusOrderByOrderDateDesc(Order.OrderStatus.SHIPPED);
    }

    public List<Order> getDeliveredOrders() {
        return orderRepository.findByStatusOrderByOrderDateDesc(Order.OrderStatus.DELIVERED);
    }

    public List<Order> getCancelledOrders() {
        return orderRepository.findByStatusOrderByOrderDateDesc(Order.OrderStatus.CANCELLED);
    }

    public void confirmOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.confirm();
            orderRepository.save(order);
        }
    }

    public void shipOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.ship();
            orderRepository.save(order);
        }
    }

    public void deliverOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.deliver();
            orderRepository.save(order);
        }
    }

    public void cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.cancel();
            orderRepository.save(order);
        }
    }

    public void addOrderItem(Long orderId, Long productId, Integer quantity) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (orderOpt.isPresent() && productOpt.isPresent()) {
            Order order = orderOpt.get();
            Product product = productOpt.get();
            
            OrderItem orderItem = new OrderItem(order, product, quantity, product.getPrice());
            order.addOrderItem(orderItem);
            
            orderRepository.save(order);
        }
    }

    public void removeOrderItem(Long orderId, Long orderItemId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.getOrderItems().removeIf(item -> item.getId().equals(orderItemId));
            orderRepository.save(order);
        }
    }

    public void updateOrderItemQuantity(Long orderId, Long orderItemId, Integer newQuantity) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            for (OrderItem item : order.getOrderItems()) {
                if (item.getId().equals(orderItemId)) {
                    item.setQuantity(newQuantity);
                    break;
                }
            }
            orderRepository.save(order);
        }
    }

    public void updateOrderTotalAmount(Long orderId, BigDecimal newTotalAmount) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setTotalAmount(newTotalAmount);
            orderRepository.save(order);
        }
    }

    public void updateOrderShippingAddress(Long orderId, String shippingAddress) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setShippingAddress(shippingAddress);
            orderRepository.save(order);
        }
    }

    public void updateOrderBillingAddress(Long orderId, String billingAddress) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setBillingAddress(billingAddress);
            orderRepository.save(order);
        }
    }

    public void updateOrderNotes(Long orderId, String notes) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setNotes(notes);
            orderRepository.save(order);
        }
    }

    public List<Order> getOrdersByUserAndStatus(User user, Order.OrderStatus status) {
        return orderRepository.findByUserAndStatusOrderByOrderDateDesc(user, status);
    }

    public List<Order> getOrdersByUserAndDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByUserAndOrderDateBetweenOrderByOrderDateDesc(user, startDate, endDate);
    }

    public List<Order> getOrdersByUserAndTotalAmountRange(User user, BigDecimal minAmount, BigDecimal maxAmount) {
        return orderRepository.findByUserAndTotalAmountBetweenOrderByOrderDateDesc(user, minAmount, maxAmount);
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.findSumOfTotalAmountByStatus(Order.OrderStatus.DELIVERED);
    }

    public BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findSumOfTotalAmountByStatusAndOrderDateBetween(Order.OrderStatus.DELIVERED, startDate, endDate);
    }

    public BigDecimal getTotalRevenueByUser(User user) {
        return orderRepository.findSumOfTotalAmountByUserAndStatus(user, Order.OrderStatus.DELIVERED);
    }

    public int getAverageOrderValue() {
        return orderRepository.findAverageOrderValueByStatus(Order.OrderStatus.DELIVERED);
    }

    public int getAverageOrderValueByUser(User user) {
        return orderRepository.findAverageOrderValueByUserAndStatus(user, Order.OrderStatus.DELIVERED);
    }

    public List<Order> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopByOrderByOrderDateDesc(pageable);
    }

    public List<Order> getRecentOrdersByUser(User user, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopByUserOrderByOrderDateDesc(user, pageable);
    }

    public List<Order> getHighValueOrders(BigDecimal threshold) {
        return orderRepository.findByTotalAmountGreaterThanOrderByOrderDateDesc(threshold);
    }

    public List<Order> getLowValueOrders(BigDecimal threshold) {
        return orderRepository.findByTotalAmountLessThanOrderByOrderDateDesc(threshold);
    }

    public boolean canCancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            return order.canBeCancelled();
        }
        return false;
    }

    public boolean canShipOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            return order.canBeShipped();
        }
        return false;
    }

    public boolean canDeliverOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            return order.canBeDelivered();
        }
        return false;
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 