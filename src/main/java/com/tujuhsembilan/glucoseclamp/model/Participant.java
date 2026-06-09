package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"sessions"}, callSuper = true)
@ToString(exclude = {"sessions"})
@Builder
public class Participant extends BaseEntity {
    
    @Id
    @Column(name = "participant_id", length = 50)
    private String participantId;
    
    @Column(name = "medical_record_no", unique = true, nullable = false, length = 100)
    private String medicalRecordNo;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "gender", length = 20)
    private String gender;
    
    @Column(name = "dob")
    private LocalDate dob;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "number_phone", length = 20)
    private String numberPhone;
    
    @OneToMany(mappedBy = "participant")
    private List<Session> sessions;
}
