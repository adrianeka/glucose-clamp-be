package com.tujuhsembilan.glucoseclamp.dto.response;

import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionActivityItemResponse {
    private String activityId;
    private LocalDateTime time;
    private String activityType;
    private String activityDesc;
    private ActivityStatus activityStatus;
}