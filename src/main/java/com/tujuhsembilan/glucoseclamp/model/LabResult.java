package com.tujuhsembilan.glucoseclamp.model;

import java.math.BigDecimal;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "lab_results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"blood_sample_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"bloodSample", "verifiedByUser"}, callSuper = true)
@ToString(exclude = {"bloodSample", "verifiedByUser"})
@Builder
public class LabResult extends BaseEntity {
    
    @Id
    @Column(name = "lab_result_id", length = 50)
    private String labResultId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_sample_id", nullable = false, unique = true)
    private BloodSample bloodSample;
    
    @Column(name = "parameter_name", nullable = false, length = 100)
    private String parameterName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedByUser;
    
    @Column(name = "value", precision = 10, scale = 2)
    private BigDecimal value;
    
    @Column(name = "reference_range_min", precision = 10, scale = 2)
    private BigDecimal referenceRangeMin;
    
    @Column(name = "reference_range_max", precision = 10, scale = 2)
    private BigDecimal referenceRangeMax;
    
    @Column(name = "unit", length = 50)
    private String unit;
    
    @Column(name = "abnormal_flag", length = 20)
    private String abnormalFlag;
}
