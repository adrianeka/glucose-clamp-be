package com.tujuhsembilan.glucoseclamp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolDetailResponse {

    @JsonProperty("protocols_detail_id")
    private String protocolsDetailId;

    @JsonProperty("protocol_id")
    private String protocolId;

    @JsonProperty("phase_code")
    private String phaseCode;

    @JsonProperty("time_interval")
    private Integer timeInterval;

    @JsonProperty("blood_raw")
    private Boolean bloodRaw;

    @JsonProperty("insulin_inject")
    private Boolean insulinInject;

    @JsonProperty("insulin_check")
    private Boolean insulinCheck;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("created_by")
    private Integer createdBy;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("updated_by")
    private Integer updatedBy;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;

    @JsonProperty("deleted_by")
    private Integer deletedBy;

    @JsonProperty("status")
    private String status;
}
