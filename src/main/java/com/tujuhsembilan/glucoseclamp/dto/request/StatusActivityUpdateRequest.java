package com.tujuhsembilan.glucoseclamp.dto.request;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusActivityUpdateRequest {
    @NotNull
    private ActivityStatus activityStatus;
}
