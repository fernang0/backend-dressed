package cl.dressed.backend.module.outfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outfits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "origin")
    private String origin;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
        if (origin == null) origin = "automatic";
    }
}