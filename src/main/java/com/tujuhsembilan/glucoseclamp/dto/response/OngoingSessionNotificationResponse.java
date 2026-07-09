package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OngoingSessionNotificationResponse {
    private boolean hasActiveSession;
    private Long sessionId;
    private String participantName;
    private String protocolName;
    private SessionNotificationItemResponse nextActivity;
}