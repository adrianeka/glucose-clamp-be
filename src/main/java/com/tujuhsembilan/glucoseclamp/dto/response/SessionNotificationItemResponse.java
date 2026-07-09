package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionNotificationItemResponse {
    private Long activityId;
    private LocalDateTime time; // Target waktu pengerjaan (akan diparse sebagai ISO String oleh FE)
    private String activityType;
    private String activityDesc;
    private String phaseCode;
    private String phaseName;
    private String activityStatus;
    private Integer minute;
}