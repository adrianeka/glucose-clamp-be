package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "anamneses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"session_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"patient", "session", "assignedByUser"}, callSuper = true)
@ToString(exclude = {"patient", "session", "assignedByUser"})
@Builder
public class Anamnesis extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anamnesis_id_seq")
    @SequenceGenerator(name = "anamnesis_id_seq", sequenceName = "anamnesis_id_seq", allocationSize = 1)
    @Column(name = "anamnesis_id")
    private Integer anamnesisId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private Session session;
    
    @Column(name = "tanggal")
    private LocalDate tanggal;
    
    @Column(name = "keluhan_utama", length = 500)
    private String keluhanUtama;
    
    @Column(name = "riwayat_penyakit", length = 1000)
    private String riwayatPenyakit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedByUser;
}
