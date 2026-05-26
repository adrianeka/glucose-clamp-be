package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "anthropometries", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"session_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"session", "assignedByUser"}, callSuper = true)
@ToString(exclude = {"session", "assignedByUser"})
@Builder
public class Anthropometry extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anthro_id_seq")
    @SequenceGenerator(name = "anthro_id_seq", sequenceName = "anthro_id_seq", allocationSize = 1)
    @Column(name = "anthro_id")
    private Integer anthroId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private Session session;
    
    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;
    
    @Column(name = "height_cm", precision = 10, scale = 2)
    private BigDecimal heightCm;
    
    @Column(name = "bmi", precision = 10, scale = 2)
    private BigDecimal bmi;
    
    @Column(name = "measured_at")
    private LocalDateTime measuredAt;
    
    @Column(name = "waist_circumference_cm", precision = 10, scale = 2)
    private BigDecimal waistCircumferenceCm;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedByUser;
}