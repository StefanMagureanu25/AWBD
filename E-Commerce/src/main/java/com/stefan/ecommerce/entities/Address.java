package com.stefan.ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address cannot exceed 255 characters")
    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank(message = "State/Province is required")
    @Size(max = 100, message = "State/Province cannot exceed 100 characters")
    @Column(name = "state_province", nullable = false)
    private String stateProvince;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(name = "country", nullable = false)
    private String country;

    @Size(max = 100, message = "Address type cannot exceed 100 characters")
    @Column(name = "address_type")
    private String addressType;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Address() {}

    public Address(User user, String streetAddress, String city, String stateProvince, String postalCode, String country) {
        this.user = user;
        this.streetAddress = streetAddress;
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.country = country;
        this.isDefault = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Address(User user, String streetAddress, String city, String stateProvince, String postalCode, String country, String addressType) {
        this.user = user;
        this.streetAddress = streetAddress;
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.country = country;
        this.addressType = addressType;
        this.isDefault = false;
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

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress);
        sb.append(", ").append(city);
        sb.append(", ").append(stateProvince);
        sb.append(" ").append(postalCode);
        sb.append(", ").append(country);
        return sb.toString();
    }

    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress).append("\n");
        sb.append(city).append(", ").append(stateProvince).append(" ").append(postalCode).append("\n");
        sb.append(country);
        return sb.toString();
    }

    public boolean isShippingAddress() {
        return "SHIPPING".equalsIgnoreCase(addressType);
    }

    public boolean isBillingAddress() {
        return "BILLING".equalsIgnoreCase(addressType);
    }

    public boolean isDefaultAddress() {
        return isDefault != null && isDefault;
    }

    public boolean isValid() {
        return streetAddress != null && !streetAddress.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               stateProvince != null && !stateProvince.trim().isEmpty() &&
               postalCode != null && !postalCode.trim().isEmpty() &&
               country != null && !country.trim().isEmpty();
    }

    public String getAddressTypeDisplay() {
        if (addressType == null) return "General";
        switch (addressType.toUpperCase()) {
            case "SHIPPING": return "Shipping Address";
            case "BILLING": return "Billing Address";
            case "HOME": return "Home Address";
            case "WORK": return "Work Address";
            default: return addressType;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return id != null && id.equals(address.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", stateProvince='" + stateProvince + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", addressType='" + addressType + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}