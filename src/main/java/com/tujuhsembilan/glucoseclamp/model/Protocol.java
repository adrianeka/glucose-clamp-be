package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "protocols")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "protocolDetails", callSuper = true)
@ToString(exclude = "protocolDetails")
@Builder
public class Protocol extends BaseEntity {
    
    @Id
    @Column(name = "protocol_id", length = 50)
    private String protocolId;
    
    @Column(name = "protocol_code", unique = true, nullable = false, length = 50)
    private String protocolCode;
    
    @Column(name = "protocol_name", nullable = false, length = 255)
    private String protocolName;
    
    @Column(name = "insulin_dose_rule", length = 500)
    private String insulinDoseRule;
    
    @Column(name = "insulin_dose_unit", length = 50)
    private String insulinDoseUnit;
    
    @Column(name = "glucose_target_min", precision = 10, scale = 2)
    private BigDecimal glucoseTargetMin;
    
    @Column(name = "glucose_target_max", precision = 10, scale = 2)
    private BigDecimal glucoseTargetMax;
    
    @Column(name = "glucose_target_unit", length = 50)
    private String glucoseTargetUnit;
    
    @Column(name = "duration_hours", precision = 10, scale = 2)
    private BigDecimal durationHours;
    
    @Column(name = "version", nullable = false)
    private Float version;
    
    @OneToMany(mappedBy = "protocol")
    private List<ProtocolDetail> protocolDetails;
}
