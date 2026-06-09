package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "global_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalConfiguration extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_config_id_seq")
    @SequenceGenerator(name = "global_config_id_seq", sequenceName = "global_config_id_seq", allocationSize = 1)
    @Column(name = "gconf_id")
    private Integer gconfId;
    
    @Column(name = "gconf_code", nullable = false, length = 100)
    private String gconfCode;
    
    @Column(name = "gconf_value", nullable = false, length = 255)
    private String gconfValue;
    
    @Column(name = "last_calibration_date")
    private LocalDateTime lastCalibrationDate;
}
