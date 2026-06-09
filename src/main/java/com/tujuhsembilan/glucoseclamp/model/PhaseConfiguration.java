package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "phase_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "phaseConfigId", callSuper = true)
@ToString(exclude = "phaseConfigId")
@Builder
public class PhaseConfiguration extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phase_config_id_seq")
    @SequenceGenerator(name = "phase_config_id_seq", sequenceName = "phase_config_id_seq", allocationSize = 1)
    @Column(name = "phase_config_id")
    private BigInteger phaseConfigId;
    
    @Column(name = "phase_config_code", nullable = false, length = 100)
    private String phaseConfigCode;
    
    @Column(name = "phase_config_name", nullable = false, length = 255)
    private String phaseConfigName;

    @Column(name = "phase_config_type", nullable = false, length = 100)
    private String phaseConfigType;

}
