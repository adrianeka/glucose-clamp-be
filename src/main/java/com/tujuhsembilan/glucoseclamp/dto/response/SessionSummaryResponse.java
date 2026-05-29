package com.tujuhsembilan.glucoseclamp.dto.response;

import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionSummaryResponse {
    private Integer sessionId;
    private String patientId;
    private String patientName;
    private String protocolId;
    private String protocolName;
    private LocalDate visitDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus sessionStatus;
    private Long totalActivities;
    private Long completedActivities;
}