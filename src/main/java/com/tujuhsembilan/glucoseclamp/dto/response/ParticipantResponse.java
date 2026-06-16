package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantResponse {
    private String participantId;
    private String medicalRecordNo;
    private String name;
    private int age;
    private String gender;
    private LocalDate dob;
    private String numberPhone;
    private String status;
    private String createdAt;
    private Integer createdBy;
    private String updatedAt;
    private Integer updatedBy;
}
