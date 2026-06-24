package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"participant", "protocol", "sessionDevices"}, callSuper = true)
@ToString(exclude = {"participant", "protocol", "sessionDevices"})
@Builder
public class Session extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_id_seq")
    @SequenceGenerator(
            name = "session_id_seq",
            sequenceName = "session_id_seq",
            allocationSize = 1
    )
    @Column(name = "session_id")
    private Long sessionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false)
    private Protocol protocol;
    
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "fasting_hour")
    private Integer fastingHour;

    @Column(name = "end_reason_category", length = 100)
    private String endReasonCategory;

    @Column(name = "end_reason_detail", length = 1000)
    private String endReasonDetail;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", length = 50)
    private SessionStatus sessionStatus;
    
    @OneToMany(mappedBy = "session")
    private List<SessionDevice> sessionDevices;
}
