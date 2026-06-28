ALTER TABLE activities
DROP CONSTRAINT IF EXISTS activities_activity_status_check;

ALTER TABLE activities
ADD CONSTRAINT activities_activity_status_check
CHECK (
    activity_status IN (
        'INQUEUE',
        'NEXT_ACTIVITY',
        'IN_PROGRESS',
        'COMPLETED'
    )
);