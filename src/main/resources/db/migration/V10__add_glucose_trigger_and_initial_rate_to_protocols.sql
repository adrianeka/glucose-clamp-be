ALTER TABLE protocols 
ADD COLUMN IF NOT EXISTS glucose_drop_trigger_percentage DECIMAL(5, 2),
ADD COLUMN IF NOT EXISTS initial_glucose_infusion_rate DECIMAL(10, 2),
ADD COLUMN IF NOT EXISTS initial_glucose_infusion_rate_unit VARCHAR(50);