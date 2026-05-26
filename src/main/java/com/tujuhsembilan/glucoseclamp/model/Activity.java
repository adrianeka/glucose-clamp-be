package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
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
    @Column(name = "activity_id", length = 50)
    private String activityId;
    
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
    
    @Column(name = "activity_status", length = 50)
    private String activityStatus;
    
    @OneToMany(mappedBy = "activity")
    private List<BloodSample> bloodSamples;
}
