package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateResponse {
    private Integer sessionId;
    private String patientId;
    private String protocolId;
    private int generatedActivityCount;
    private List<String> activityIds;
}