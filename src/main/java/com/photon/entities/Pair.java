package com.photon.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pairs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"sender", "receiver"})
@ToString(exclude = {"sender", "receiver"})
public class Pair {

    @Id
    @GeneratedValue
    private Long id;

    // Many-to-One relationship with User (sender)
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // Many-to-One relationship with User (receiver)
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private Boolean isAccepted = false;
}