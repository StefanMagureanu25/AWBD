package com.stefan.ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number cannot exceed 50 characters")
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Total amount format is invalid")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order() {}

    public Order(String orderNumber, User user, BigDecimal totalAmount) {
        this.orderNumber = orderNumber;
        this.user = user;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
        this.orderDate = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }

    public LocalDateTime getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(LocalDateTime deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public LocalDateTime getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(LocalDateTime cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    public void clearOrderItems() {
        this.orderItems.clear();
    }

    public int getItemCount() {
        return orderItems.size();
    }

    public boolean hasItems() {
        return !orderItems.isEmpty();
    }

    public boolean isPending() {
        return OrderStatus.PENDING.equals(status);
    }

    public boolean isConfirmed() {
        return OrderStatus.CONFIRMED.equals(status);
    }

    public boolean isShipped() {
        return OrderStatus.SHIPPED.equals(status);
    }

    public boolean isDelivered() {
        return OrderStatus.DELIVERED.equals(status);
    }

    public boolean isCancelled() {
        return OrderStatus.CANCELLED.equals(status);
    }

    public boolean canBeCancelled() {
        return OrderStatus.PENDING.equals(status) || OrderStatus.CONFIRMED.equals(status);
    }

    public boolean canBeShipped() {
        return OrderStatus.CONFIRMED.equals(status);
    }

    public boolean canBeDelivered() {
        return OrderStatus.SHIPPED.equals(status);
    }

    public void confirm() {
        if (isPending()) {
            this.status = OrderStatus.CONFIRMED;
        }
    }

    public void ship() {
        if (canBeShipped()) {
            this.status = OrderStatus.SHIPPED;
            this.shippedDate = LocalDateTime.now();
        }
    }

    public void deliver() {
        if (canBeDelivered()) {
            this.status = OrderStatus.DELIVERED;
            this.deliveredDate = LocalDateTime.now();
        }
    }

    public void cancel() {
        if (canBeCancelled()) {
            this.status = OrderStatus.CANCELLED;
            this.cancelledDate = LocalDateTime.now();
        }
    }

    public String getStatusDisplayName() {
        if (status == null) return "Unknown";
        return status.getDisplayName();
    }

    public String getFormattedOrderDate() {
        if (orderDate == null) return "Unknown";
        return orderDate.toString();
    }

    public String getFormattedTotalAmount() {
        if (totalAmount == null) return "$0.00";
        return "$" + totalAmount.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id != null && id.equals(order.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                '}';
    }

    public enum OrderStatus {
        PENDING("Pending"),
        CONFIRMED("Confirmed"),
        SHIPPED("Shipped"),
        DELIVERED("Delivered"),
        CANCELLED("Cancelled");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}