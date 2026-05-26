package com.tujuhsembilan.glucoseclamp.model;

import com.tujuhsembilan.glucoseclamp.model.base.BaseEntity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "sessionDevices", callSuper = true)
@ToString(exclude = "sessionDevices")
@Builder
public class Device extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_id_seq")
    @SequenceGenerator(name = "device_id_seq", sequenceName = "device_id_seq", allocationSize = 1)
    @Column(name = "device_id")
    private Integer deviceId;
    
    @Column(name = "device_type", nullable = false, length = 100)
    private String deviceType;
    
    @Column(name = "device_brand", length = 100)
    private String deviceBrand;
    
    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;
    
    @Column(name = "last_calibration_date")
    private LocalDateTime lastCalibrationDate;
    
    @OneToMany(mappedBy = "device")
    private List<SessionDevice> sessionDevices;
}
