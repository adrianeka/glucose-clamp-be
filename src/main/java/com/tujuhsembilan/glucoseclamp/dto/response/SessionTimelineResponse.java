package com.tujuhsembilan.glucoseclamp.dto.response;

import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionTimelineResponse {
    private Integer sessionId;
    private String patientId;
    private String patientName;
    private String protocolId;
    private String protocolName;
    private LocalDate visitDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus sessionStatus;
    private int totalActivities;
    private int completedActivities;
    private int progressPercentage;
    private SessionActivityItemResponse nextActivity;
    private List<SessionActivityItemResponse> activities;
}