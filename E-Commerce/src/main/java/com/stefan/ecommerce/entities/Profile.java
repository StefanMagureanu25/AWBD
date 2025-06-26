package com.stefan.ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    @Column(name = "last_name")
    private String lastName;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "Invalid phone number format")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 10, message = "Gender cannot exceed 10 characters")
    @Column(name = "gender")
    private String gender;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Size(max = 255, message = "Profile picture URL cannot exceed 255 characters")
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    @Column(name = "location")
    private String location;

    @Size(max = 100, message = "Website cannot exceed 100 characters")
    @Column(name = "website")
    private String website;

    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications")
    private Boolean smsNotifications = false;

    @Column(name = "marketing_emails")
    private Boolean marketingEmails = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Profile() {}

    public Profile(User user) {
        this.user = user;
        this.emailNotifications = true;
        this.smsNotifications = false;
        this.marketingEmails = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Profile(User user, String firstName, String lastName) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailNotifications = true;
        this.smsNotifications = false;
        this.marketingEmails = false;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public Boolean getSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    public Boolean getMarketingEmails() {
        return marketingEmails;
    }

    public void setMarketingEmails(Boolean marketingEmails) {
        this.marketingEmails = marketingEmails;
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

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "Unknown User";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    public String getDisplayName() {
        String fullName = getFullName();
        if (!"Unknown User".equals(fullName)) {
            return fullName;
        }
        return user != null ? user.getUsername() : "Unknown User";
    }

    public String getInitials() {
        StringBuilder initials = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            initials.append(firstName.trim().charAt(0));
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            initials.append(lastName.trim().charAt(0));
        }
        return initials.toString().toUpperCase();
    }

    public boolean hasProfilePicture() {
        return profilePictureUrl != null && !profilePictureUrl.trim().isEmpty();
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    public boolean hasLocation() {
        return location != null && !location.trim().isEmpty();
    }

    public boolean hasWebsite() {
        return website != null && !website.trim().isEmpty();
    }

    public boolean hasBio() {
        return bio != null && !bio.trim().isEmpty();
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotifications != null && emailNotifications;
    }

    public boolean isSmsNotificationsEnabled() {
        return smsNotifications != null && smsNotifications;
    }

    public boolean isMarketingEmailsEnabled() {
        return marketingEmails != null && marketingEmails;
    }

    public int getAge() {
        if (dateOfBirth == null) {
            return -1;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public boolean isAdult() {
        int age = getAge();
        return age >= 18;
    }

    public String getGenderDisplay() {
        if (gender == null) return "Not specified";
        switch (gender.toLowerCase()) {
            case "m":
            case "male": return "Male";
            case "f":
            case "female": return "Female";
            case "o":
            case "other": return "Other";
            default: return gender;
        }
    }

    public boolean isComplete() {
        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    public double getCompletionPercentage() {
        int totalFields = 8;
        int completedFields = 0;

        if (firstName != null && !firstName.trim().isEmpty()) completedFields++;
        if (lastName != null && !lastName.trim().isEmpty()) completedFields++;
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) completedFields++;
        if (dateOfBirth != null) completedFields++;
        if (gender != null && !gender.trim().isEmpty()) completedFields++;
        if (bio != null && !bio.trim().isEmpty()) completedFields++;
        if (location != null && !location.trim().isEmpty()) completedFields++;
        if (website != null && !website.trim().isEmpty()) completedFields++;

        return (double) completedFields / totalFields * 100;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return id != null && id.equals(profile.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailNotifications=" + emailNotifications +
                '}';
    }
}