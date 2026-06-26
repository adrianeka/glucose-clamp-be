package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionActivityItemResponse {
    private Long activityId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;
    private String activityType;
    private String activityDesc;
    private String phaseCode;
    private String phaseName;
    private ActivityStatus activityStatus;
    private Integer minute;
    private String scheduleCode;
    private String phaseType;
    private List<LabResultItemResultResponse> labResults;
}