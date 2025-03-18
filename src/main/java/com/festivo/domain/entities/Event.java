package com.festivo.domain.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "start_date")
    private LocalDateTime startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDateTime endDate;

    @OneToOne
    private Address address;

    @ManyToOne
    @JoinColumn(nullable = false, name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "event_organizers",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> organizers;

    @ManyToMany(mappedBy = "invitations")
    private List<User> invitedUsers;

    @Column(nullable = true, name = "party_logo")
    private String partyLogo;

    @Column(nullable = true, name = "party_banner")
    private String partyBanner;

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
