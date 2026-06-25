DO $$ 
BEGIN 
    -- 1. Ubah kolom confirmation_rate_min_kg lamun aya di skéma anu nuju dianggo (glucose_clamp)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_schema = current_schema() -- Mastikeun ngan mariksa skéma nu nuju aktip
               AND table_name = 'infusion_monitorings' 
               AND column_name = 'confirmation_rate_min_kg') THEN
        
        ALTER TABLE infusion_monitorings RENAME COLUMN confirmation_rate_min_kg TO actual_gir;
    END IF;

    -- 2. Ubah kolom rate_min_kg lamun aya di skéma anu nuju dianggo (glucose_clamp)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_schema = current_schema() -- Mastikeun ngan mariksa skéma nu nuju aktip
               AND table_name = 'infusion_monitorings' 
               AND column_name = 'rate_min_kg') THEN
        
        ALTER TABLE infusion_monitorings RENAME COLUMN rate_min_kg TO recommended_gir;
    END IF;
END $$;