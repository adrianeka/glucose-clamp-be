package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {
    private String patientId;
    private String medicalRecordNo;
    private String name;
    private String gender;
    private LocalDate dob;
    private String numberPhone;
    private String status;
    private String createdAt;
    private Integer createdBy;
    private String updatedAt;
    private Integer updatedBy;
}
