package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "global_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "gconfId", callSuper = true)
@ToString(exclude = "gconfId")
@Builder
public class GlobalConfiguration extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_config_id_seq")
    @SequenceGenerator(name = "global_config_id_seq", sequenceName = "global_config_id_seq", allocationSize = 1)
    @Column(name = "gconf_id")
    private BigInteger gconfId;
    
    @Column(name = "gconf_code", nullable = false, length = 100)
    private String gconfCode;
    
    @Column(name = "gconf_value", nullable = false, length = 255)
    private String gconfValue;

    @Column(name = "gconf_title", length = 255)
    private String gconfTitle;

    @Column(name = "gconf_desc", length = 500)
    private String gconfDescription;

}
