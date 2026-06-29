package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "blood_samples")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "activity", callSuper = true)
@ToString(exclude = "activity")
@Builder
public class BloodSample extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blood_sample_id")
    private Long bloodSampleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Column(name = "sample_code", length = 100)
    private String sampleCode;

    @Column(name = "collected_by")
    private Integer collectedBy;

    @Column(name = "sample_time")
    private LocalDateTime sampleTime;

    @Column(name = "sample_type", length = 100)
    private String sampleType;

    @Column(name = "tube_type", length = 100)
    private String tubeType;

    @Column(name = "volume_ml")
    private Integer volumeMl;

    @OneToMany(mappedBy = "bloodSample")
    private List<LabResult> labResults;
}
