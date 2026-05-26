package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_samples")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"activity", "collectedByUser"}, callSuper = true)
@ToString(exclude = {"activity", "collectedByUser"})
@Builder
public class BloodSample extends BaseEntity {
    
    @Id
    @Column(name = "blood_sample_id", length = 50)
    private String bloodSampleId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by")
    private User collectedByUser;
    
    @Column(name = "sample_time")
    private LocalDateTime sampleTime;
    
    @Column(name = "sample_type", length = 50)
    private String sampleType;
    
    @Column(name = "tube_type", length = 50)
    private String tubeType;
    
    @Column(name = "volume_ml", precision = 10, scale = 2)
    private BigDecimal volumeMl;
}
