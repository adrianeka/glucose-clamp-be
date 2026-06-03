package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "infusion_monitorings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "session", callSuper = true)
@ToString(exclude = "session")
@Builder
public class InfusionMonitoring extends BaseEntity {
    
    @Id
    @Column(name = "infusion_id", length = 10)
    private String infusionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
    
    @Column(name = "time")
    private LocalDateTime time;
    
    @Column(name = "glucose_value", precision = 10, scale = 2)
    private BigDecimal glucoseValue;
    
    @Column(name = "confirmation_rate_min_kg", precision = 10, scale = 2)
    private BigDecimal confirmationRateMinKg;
    
    @Column(name = "rate_min_kg", precision = 10, scale = 2)
    private BigDecimal rateMinKg;
    
    @Column(name = "flow_rate_ml_hr", precision = 10, scale = 2)
    private BigDecimal flowRateMlHr;
    
    @Column(name = "adjustment_note", length = 500)
    private String adjustmentNote;

    @Column(name = "monitored_by")
    private Integer monitoredBy;
}
