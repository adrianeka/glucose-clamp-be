ALTER TABLE sampling_schedules
ADD COLUMN IF NOT EXISTS schedule_code VARCHAR(255);

ALTER TABLE sampling_schedules
ADD COLUMN IF NOT EXISTS phase_duration Integer;

WITH numbered AS (
    SELECT
        sampling_schedule_id,
        CASE
            WHEN blood_raw = TRUE THEN
                'GD-' || ROW_NUMBER() OVER (
                    PARTITION BY protocol_id, blood_raw
                    ORDER BY relative_minute, sampling_schedule_id
                )
            ELSE
                'T-' || ROW_NUMBER() OVER (
                    PARTITION BY protocol_id
                    ORDER BY relative_minute, sampling_schedule_id
                )
        END AS code
    FROM sampling_schedules
)
UPDATE sampling_schedules s
SET schedule_code = n.code
FROM numbered n
WHERE s.sampling_schedule_id = n.sampling_schedule_id;

ALTER TABLE sampling_schedules
ALTER COLUMN schedule_code SET NOT NULL;