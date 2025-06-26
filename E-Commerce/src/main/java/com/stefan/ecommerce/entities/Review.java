package com.stefan.ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

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

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "verified_purchase")
    private Boolean verifiedPurchase = false;

    @Column(name = "helpful_votes")
    private Integer helpfulVotes = 0;

    @Column(name = "total_votes")
    private Integer totalVotes = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Review() {}

    public Review(User user, Product product, Integer rating, String title) {
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.title = title;
        this.verifiedPurchase = false;
        this.helpfulVotes = 0;
        this.totalVotes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Review(User user, Product product, Integer rating, String title, String comment) {
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.verifiedPurchase = false;
        this.helpfulVotes = 0;
        this.totalVotes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public Integer getHelpfulVotes() {
        return helpfulVotes;
    }

    public void setHelpfulVotes(Integer helpfulVotes) {
        this.helpfulVotes = helpfulVotes;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRatingStars() {
        if (rating == null) return "";
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public String getRatingText() {
        if (rating == null) return "No rating";
        switch (rating) {
            case 1: return "Poor";
            case 2: return "Fair";
            case 3: return "Good";
            case 4: return "Very Good";
            case 5: return "Excellent";
            default: return "Unknown";
        }
    }

    public double getHelpfulPercentage() {
        if (totalVotes == null || totalVotes == 0) {
            return 0.0;
        }
        return (double) helpfulVotes / totalVotes * 100;
    }

    public boolean isVerifiedPurchase() {
        return verifiedPurchase != null && verifiedPurchase;
    }

    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }

    public boolean isHelpful() {
        return getHelpfulPercentage() >= 50.0;
    }

    public void incrementHelpfulVotes() {
        this.helpfulVotes = (this.helpfulVotes != null ? this.helpfulVotes : 0) + 1;
        this.totalVotes = (this.totalVotes != null ? this.totalVotes : 0) + 1;
    }

    public void incrementTotalVotes() {
        this.totalVotes = (this.totalVotes != null ? this.totalVotes : 0) + 1;
    }

    public String getFormattedCreatedDate() {
        if (createdAt == null) return "Unknown";
        return createdAt.toString();
    }

    public String getUserDisplayName() {
        return user != null ? user.getUsername() : "Anonymous";
    }

    public String getProductName() {
        return product != null ? product.getName() : "Unknown Product";
    }

    public boolean isValid() {
        return rating != null && rating >= 1 && rating <= 5 &&
               title != null && !title.trim().isEmpty() &&
               user != null && product != null;
    }

    public boolean isHighRating() {
        return rating != null && rating >= 4;
    }

    public boolean isLowRating() {
        return rating != null && rating <= 2;
    }

    public boolean isRecent() {
        if (createdAt == null) return false;
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return createdAt.isAfter(thirtyDaysAgo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id != null && id.equals(review.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", productId=" + (product != null ? product.getId() : "null") +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", verifiedPurchase=" + verifiedPurchase +
                '}';
    }
}