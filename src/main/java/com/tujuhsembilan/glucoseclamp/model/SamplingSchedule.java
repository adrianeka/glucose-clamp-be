package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "sampling_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "protocol", callSuper = true)
@ToString(exclude = "protocol")
@Builder
public class SamplingSchedule extends BaseEntity {
    
    @Id
    @Column(name = "sampling_schedule_id", length = 50)
    private String samplingScheduleId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false)
    private Protocol protocol;
    
    @Column(name = "phase_code", length = 50)
    private String phaseCode;

    @Column(name = "phase_name", length = 255)
    private String phaseName;

    @Column(name = "phase_type", length = 100)
    private String phaseType;

    @Column(name = "relative_minute")
    private Integer relativeMinute; // Menyimpan nilai 0, 10, 30
    
    @Column(name = "time_interval")
    private Integer timeInterval;
    
    @Column(name = "blood_raw")
    private Boolean bloodRaw;
    
    @Column(name = "insulin_inject")
    private Boolean insulinInject;
    
    @Column(name = "pk_sample_collection")
    private Boolean pkSampleCollection;
}