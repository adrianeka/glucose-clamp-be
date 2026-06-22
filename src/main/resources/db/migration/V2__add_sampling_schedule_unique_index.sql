CREATE UNIQUE INDEX IF NOT EXISTS
uk_sampling_schedule_protocol_phase_minute
ON  sampling_schedules (
    protocol_id,
    relative_minute
)
WHERE deleted_at IS NULL;