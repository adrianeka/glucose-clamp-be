package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "protocol_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "protocol", callSuper = true)
@ToString(exclude = "protocol")
@Builder
public class ProtocolDetail extends BaseEntity {
    
    @Id
    @Column(name = "protocol_detail_id", length = 50)
    private String protocolDetailId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false)
    private Protocol protocol;
    
    @Column(name = "phase_code", length = 50)
    private String phaseCode;
    
    @Column(name = "time_interval")
    private Integer timeInterval;
    
    @Column(name = "blood_raw")
    private Boolean bloodRaw;
    
    @Column(name = "insulin_inject")
    private Boolean insulinInject;
    
    @Column(name = "insulin_check")
    private Boolean insulinCheck;
}