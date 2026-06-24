package com.tujuhsembilan.glucoseclamp.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class BulkUpdateSamplingScheduleRequest {
    private List<UpdateItem> items;

    @Data
    public static class UpdateItem {
        private Long id;
        private Boolean bloodRaw;
        private Boolean insulinInject;
        private Boolean pkSampleCollection;
    }
}