package com.stefan.ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "notes")
    private String notes;

    public Wishlist() {}

    public Wishlist(User user, Product product) {
        this.user = user;
        this.product = product;
        this.addedAt = LocalDateTime.now();
    }

    public Wishlist(User user, Product product, String notes) {
        this.user = user;
        this.product = product;
        this.notes = notes;
        this.addedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.addedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }

    public String getFormattedAddedDate() {
        if (addedAt == null) return "Unknown";
        return addedAt.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wishlist wishlist = (Wishlist) o;
        return id != null && id.equals(wishlist.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Wishlist{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", productId=" + (product != null ? product.getId() : "null") +
                ", addedAt=" + addedAt +
                '}';
    }
} 