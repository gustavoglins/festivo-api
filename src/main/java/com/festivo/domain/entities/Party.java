package com.festivo.domain.entities;

import com.festivo.shared.enums.PartyStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "tb_party")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false, name = "date")
    private LocalDate date;

    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalTime endTime;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Address address;

    @ManyToOne
    @JoinColumn(nullable = false, name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "party_organizers",
            joinColumns = @JoinColumn(name = "party_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> organizers;

    @ManyToMany(mappedBy = "invitations")
    private List<User> guests;

//    @Basic(fetch = FetchType.LAZY)
//    @Column(name = "banner", columnDefinition = "bytea")
//    private byte[] banner;

    @Column(name = "banner_key")
    private String bannerKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
