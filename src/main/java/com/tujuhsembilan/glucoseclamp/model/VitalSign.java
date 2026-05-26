package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vital_signs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"session", "assignedByUser"}, callSuper = true)
@ToString(exclude = {"session", "assignedByUser"})
@Builder
public class VitalSign extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vital_id_seq")
    @SequenceGenerator(name = "vital_id_seq", sequenceName = "vital_id_seq", allocationSize = 1)
    @Column(name = "vital_id")
    private Integer vitalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
    
    @Column(name = "measured_at")
    private LocalDateTime measuredAt;
    
    @Column(name = "systolic")
    private Integer systolic;
    
    @Column(name = "diastolic")
    private Integer diastolic;
    
    @Column(name = "pulse")
    private Integer pulse;
    
    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;
    
    @Column(name = "temperature_c", precision = 10, scale = 2)
    private BigDecimal temperatureC;
    
    @Column(name = "spo2", precision = 10, scale = 2)
    private BigDecimal spo2;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedByUser;
}
