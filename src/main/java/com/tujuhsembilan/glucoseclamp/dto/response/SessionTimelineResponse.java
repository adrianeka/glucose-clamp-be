package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String participantId;
    private String participantName;
    private String protocolId;
    private String protocolName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate visitDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    private SessionStatus sessionStatus;
    private int totalActivities;
    private int completedActivities;
    private int progressPercentage;
    private List<SessionActivityItemResponse> nextActivities;
    private List<SessionActivityItemResponse> activities;
}