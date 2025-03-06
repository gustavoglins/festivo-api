package com.festivo.domain.entities;

import com.festivo.api.request.user.UserSignupRequestDTO;
import com.festivo.shared.enums.AuthRoles;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true, unique = true, name = "phone_number", length = 25)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = true, name = "profile_picture")
    private String profilePicture;

    @OneToMany(mappedBy = "creator")
    @Column(nullable = false)
    private List<Event> createdEvents;

    @ManyToMany(mappedBy = "organizers")
    @Column(nullable = true, name = "organized_events")
    private List<Event> organizedEvents;

    @ManyToMany
    @JoinTable(
            name = "event_invitations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> invitations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private AuthRoles authRole;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false, name = "last_login")
    private LocalDateTime lastLogin;

    public User() {

    }

    public User(UserSignupRequestDTO userSignupRequestDTO) {
        this.fullName = userSignupRequestDTO.fullName();
        this.email = userSignupRequestDTO.email();
        this.phoneNumber = userSignupRequestDTO.phoneNumber();
        this.password = userSignupRequestDTO.password();
        this.birthDate = userSignupRequestDTO.birthDate();
        this.profilePicture = userSignupRequestDTO.profilePicture();

        this.createdEvents = new ArrayList<>();
        this.organizedEvents = new ArrayList<>();
        this.invitations = new ArrayList<>();
        this.notifications = new ArrayList<>();

        this.authRole = AuthRoles.COMMON_USER;
        this.isActive = Boolean.TRUE;
        this.lastLogin = LocalDateTime.now();
    }

    public User(String fullName, String email, String phoneNumber, String password, LocalDate birthDate, String profilePicture) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.birthDate = birthDate;
        this.profilePicture = profilePicture;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Event> getCreatedEvents() {
        return createdEvents;
    }

    public List<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    public List<Event> getInvitations() {
        return invitations;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public AuthRoles getAuthRole() {
        return authRole;
    }

    public void setAuthRole(AuthRoles authRole) {
        this.authRole = authRole;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.authRole == AuthRoles.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
