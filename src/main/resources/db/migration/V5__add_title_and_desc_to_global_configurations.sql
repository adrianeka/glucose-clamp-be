-- 1. Tambah kolom
ALTER TABLE global_configurations 
ADD COLUMN IF NOT EXISTS gconf_title VARCHAR(255),
ADD COLUMN IF NOT EXISTS gconf_desc TEXT;

-- 2. Isi/Update data seed
INSERT INTO global_configurations (
    gconf_id, gconf_code, gconf_title, gconf_desc, gconf_value, 
    created_at, created_by, updated_at, updated_by, status
) 
VALUES (
    1, 
    'ACTIVITY_CONFIRMATION_TIMER', 
    'Activity Confirmation Timer (seconds)', 
    'Configure the countdown duration before an activity can be confirmed.', 
    '60', 
    '2026-05-21 07:10:00', 1, 
    '2026-05-21 07:10:00', 1, 
    'ACTIVE'
) 
ON CONFLICT (gconf_id) 
DO UPDATE SET 
    gconf_code = EXCLUDED.gconf_code, 
    gconf_title = EXCLUDED.gconf_title,
    gconf_desc = EXCLUDED.gconf_desc,
    gconf_value = EXCLUDED.gconf_value, 
    updated_at = EXCLUDED.updated_at, 
    updated_by = EXCLUDED.updated_by, 
    status = EXCLUDED.status;