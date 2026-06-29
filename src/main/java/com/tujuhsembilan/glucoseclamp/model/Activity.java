package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"session", "actor", "bloodSamples"}, callSuper = true)
@ToString(exclude = {"session", "actor", "bloodSamples"})
@Builder
public class Activity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_id_seq")
    @SequenceGenerator(
            name = "activity_id_seq",
            sequenceName = "activity_id_seq",
            allocationSize = 1
    )
    @Column(name = "activity_id")
    private Long activityId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;
    
    @Column(name = "time")
    private LocalDateTime time;
    
    @Column(name = "activity_type", length = 100)
    private String activityType;
    
    @Column(name = "activity_desc", length = 500)
    private String activityDesc;

    @Column(name = "phase_name")
    private String phaseName;

    @Column(name = "phase_code")
    private String phaseCode;

    @Column(name = "phase_type")
    private String phaseType;

    @Column(name = "schedule_code")
    private String scheduleCode;

    @Column(name = "minute")
    private Integer minute;
    
    @Column(name = "activity_status", length = 50)
    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus;
    
    @OneToMany(mappedBy = "activity")
    private List<BloodSample> bloodSamples;
}
