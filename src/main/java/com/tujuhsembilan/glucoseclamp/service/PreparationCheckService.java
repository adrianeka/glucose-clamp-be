package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.PreparationCheckRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.PreparationCheckResponse;
import com.tujuhsembilan.glucoseclamp.model.Anamnesis;
import com.tujuhsembilan.glucoseclamp.model.Anthropometry;
import com.tujuhsembilan.glucoseclamp.model.VitalSign;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreparationCheckService {

    private final VitalSignsService vitalSignsService;
    private final AnamnesisService anamnesisService;
    private final AnthropometryService anthropometryService;
    private final ActivityService activityService;

    @Transactional
    public ApiDataResponseBuilder submitPreparation(
            PreparationCheckRequest request
    ) {

        VitalSign vital = vitalSignsService.save(
                request.getVitalSign()
        );

        Anamnesis anamnesis = anamnesisService.save(
                request.getAnamnesis()
        );

        Anthropometry anthropometry =
                anthropometryService.save(
                        request.getAnthropometry()
                );

        activityService.completeActivity(
                request.getActivityId()
        );

        PreparationCheckResponse response =
                PreparationCheckResponse.builder()
                    .vitalSign(
                        vitalSignsService
                                .mapToResponse(vital)
                    )
                    .anamnesis(
                        anamnesisService
                                .mapToResponse(anamnesis)
                    )
                    .anthropometry(
                        anthropometryService
                                .mapToResponse(anthropometry)
                    )
                    .build();
        return ApiDataResponseBuilder.builder()
            .data(response)
            .message(
                    "Preparation berhasil disimpan"
            )
            .statusCode(
                    HttpStatus.CREATED.value()
            )
            .status(
                    HttpStatus.CREATED
            )
            .build();
    }
}