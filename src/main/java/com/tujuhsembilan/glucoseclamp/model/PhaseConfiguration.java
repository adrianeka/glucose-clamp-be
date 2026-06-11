package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "phase_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "phaseConfId", callSuper = true)
@ToString(exclude = "phaseConfId")
@Builder
public class PhaseConfiguration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phase_conf_id_seq")
    @SequenceGenerator(name = "phase_conf_id_seq", sequenceName = "phase_conf_id_seq", allocationSize = 1)
    @Column(name = "phase_conf_id")
    private Long phaseConfId;

    @Column(name = "phase_conf_priority", nullable = false, unique = true)
    private Integer phaseConfPriority;

    @Column(name = "phase_conf_code", nullable = false, unique = true, length = 100)
    private String phaseConfCode;

    @Column(name = "phase_conf_name", nullable = false, length = 255)
    private String phaseConfName;

    @Column(name = "phase_conf_type", nullable = false, length = 100)
    private String phaseConfType;
}
