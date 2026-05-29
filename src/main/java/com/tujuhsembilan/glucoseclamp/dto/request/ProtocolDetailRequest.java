package com.tujuhsembilan.glucoseclamp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolDetailRequest {

    @JsonProperty("protocols_detail_id")
    @NotBlank(message = "protocols_detail_id is required")
    private String protocolsDetailId;

    @JsonProperty("protocol_id")
    private String protocolId;

    @JsonProperty("phase_code")
    @NotBlank(message = "phase_code is required")
    private String phaseCode;

    @JsonProperty("time_interval")
    @NotNull(message = "time_interval is required")
    private Integer timeInterval;

    @JsonProperty("blood_raw")
    @NotNull(message = "blood_raw is required")
    private Boolean bloodRaw;

    @JsonProperty("insulin_inject")
    @NotNull(message = "insulin_inject is required")
    private Boolean insulinInject;

    @JsonProperty("insulin_check")
    @NotNull(message = "insulin_check is required")
    private Boolean insulinCheck;
}
