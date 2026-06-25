DO $$ 
BEGIN 
    -- Ubah kolom confirmation_rate_min_kg jika ada
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='infusion_monitorings' AND column_name='confirmation_rate_min_kg') THEN
        ALTER TABLE infusion_monitorings RENAME COLUMN confirmation_rate_min_kg TO actual_gir;
    END IF;

    -- Ubah kolom rate_min_kg jika ada
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='infusion_monitorings' AND column_name='rate_min_kg') THEN
        ALTER TABLE infusion_monitorings RENAME COLUMN rate_min_kg TO recommended_gir;
    END IF;
END $$;