package com.festivo.domain.entities;

import com.festivo.api.request.user.UserSignupRequestDTO;
import com.festivo.shared.enums.AuthRoles;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data
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

    @Column(nullable = false, unique = true, name = "phone_number", length = 25)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_picture", columnDefinition = "bytea")
    private byte[] profilePicture;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends;

    @ManyToMany(mappedBy = "friends")
    private Set<User> friendOf;

    @OneToMany(mappedBy = "creator")
    @Column(nullable = false)
    private List<Party> createdParties;

    @ManyToMany(mappedBy = "organizers")
    @Column(name = "organized_parties")
    private List<Party> organizedParties;

    @ManyToMany
    @JoinTable(
            name = "part_invitations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "party_id")
    )
    private List<Party> invitations;

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

        this.createdParties = new ArrayList<>();
        this.organizedParties = new ArrayList<>();
        this.invitations = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.friends = new HashSet<>();
        this.friendOf = new HashSet<>();

        this.authRole = AuthRoles.COMMON_USER;
        this.isActive = Boolean.TRUE;
        this.lastLogin = LocalDateTime.now();
    }

//    public User(String fullName, String email, String phoneNumber, String password, LocalDate birthDate) {
//        this.fullName = fullName;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.password = password;
//        this.birthDate = birthDate;
//    }

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
}
