package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateResponse {
    private Long sessionId;
    private String participantId;
    private Long protocolId;
    private int generatedActivityCount;
    private List<Long> activityIds;
    private Integer vitalId;
    private Integer anamnesisId;
    private Integer anthropometryId;
    private List<Integer> sessionDeviceIds;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
}