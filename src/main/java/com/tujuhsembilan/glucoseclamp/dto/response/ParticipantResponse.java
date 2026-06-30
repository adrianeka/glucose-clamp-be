package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String age;
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String numberPhone;
    private String status;
    private String createdAt;
    private Integer createdBy;
    private String updatedAt;
    private Integer updatedBy;
    
    private String createdByName;
    private String updatedByName;
}