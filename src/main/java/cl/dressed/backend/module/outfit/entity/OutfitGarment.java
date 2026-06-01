package cl.dressed.backend.module.outfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "outfit_garments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OutfitGarment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outfit_id", nullable = false)
    private Long outfitId;

    @Column(name = "garment_id", nullable = false)
    private Integer garmentId;

    @Column(name = "role")
    private String role;
}