package com.tujuhsembilan.glucoseclamp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceUpdateRequest {

    @Schema(description = "Tipe perangkat, misal: Glucometer, Insulin Pump", example = "Glucometer")
    @NotBlank(message = "device_type is required")
    private String deviceType;

    @Schema(description = "Merek perangkat, misal: Accu-Chek, Medtronic", example = "Accu-Chek")
    @NotBlank(message = "device_brand is required")
    private String deviceBrand;

    @Schema(description = "Nomor seri unik perangkat", example = "SN123456789")
    @NotBlank(message = "serial_number is required")
    private String serialNumber;

    @Schema(description = "Tanggal kalibrasi terakhir dengan format YYYY-MM-DDTHH:mm:ss", example = "2024-06-01T10:00:00")
    @NotBlank(message = "last_calibration_date is required")
    private String lastCalibrationDate;
}
