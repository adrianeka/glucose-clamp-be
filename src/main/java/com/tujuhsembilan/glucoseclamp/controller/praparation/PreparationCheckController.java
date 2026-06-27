package com.tujuhsembilan.glucoseclamp.controller.praparation;

import com.tujuhsembilan.glucoseclamp.dto.request.PreparationCheckRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.PreparationCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preparation-check")
@RequiredArgsConstructor
public class PreparationCheckController {

    private final PreparationCheckService preparationCheckService;

    @PostMapping
    public ResponseEntity<ApiDataResponseBuilder> submitPreparation(
            @Valid @RequestBody PreparationCheckRequest request
    ) {

        ApiDataResponseBuilder response =
                preparationCheckService.submitPreparation(
                        request
                );

        return ResponseEntity
                .status(response.getStatus())
                .body(response);

    }
}
