DO $$
BEGIN
    -- Periksa apakah tabel 'access_menus' dan 'role_access' sudah ada di database
    IF EXISTS (
        SELECT FROM information_schema.tables 
        WHERE table_schema = current_schema() AND table_name = 'access_menus'
    ) AND EXISTS (
        SELECT FROM information_schema.tables 
        WHERE table_schema = current_schema() AND table_name = 'role_access'
    ) THEN

        -- 1. HAPUS DATA LAMA JIKA TABEL EXIST
        EXECUTE 'DELETE FROM role_access';
        EXECUTE 'DELETE FROM access_menus';

        -- 2. INSERT SEED DATA KE ACCESS_MENUS
        EXECUTE 'INSERT INTO access_menus (menu_id, menu_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES
        (1,  ''USER'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (2,  ''ROLE'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (3,  ''PARTICIPANT'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (4,  ''PROTOCOL'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (5,  ''PHASECONFIGURATION'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (6,  ''SAMPLINGSCHEDULE'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (7,  ''SESSION'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (8,  ''SESSIONDEVICE'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (9,  ''INFUSIONMONITORING'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (10, ''LABRESULT'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (11, ''BLOODSAMPLE'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (12, ''VITALSIGN'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (13, ''ANTHROPOMETRY'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (14, ''ANAMNESIS'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (15, ''DEVICE'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (16, ''GLOBALCONFIGURATION'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (17, ''ACTIVITY'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (18, ''ACCESSMENU'', ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE'')
        ON CONFLICT (menu_id) DO UPDATE SET 
            menu_name = EXCLUDED.menu_name, 
            updated_at = EXCLUDED.updated_at, 
            updated_by = EXCLUDED.updated_by, 
            status = EXCLUDED.status';

        -- 3. INSERT SEED DATA KE ROLE_ACCESS
        EXECUTE 'INSERT INTO role_access (role_access_id, role_id, menu_id, can_view, can_add, can_edit, can_delete, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES
        -- SuperUser (Role ID = 1)
        (1,  1, 1,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (2,  1, 2,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (3,  1, 3,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (4,  1, 4,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (5,  1, 5,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (6,  1, 6,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (7,  1, 7,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (8,  1, 8,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (9,  1, 9,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (10, 1, 10, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (11, 1, 11, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (12, 1, 12, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (13, 1, 13, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (14, 1, 14, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (15, 1, 15, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (16, 1, 16, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (17, 1, 17, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (18, 1, 18, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),

        -- Admin (Role ID = 2)
        (19, 2, 1,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (20, 2, 2,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (21, 2, 3,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (22, 2, 4,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (23, 2, 5,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (24, 2, 6,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (25, 2, 7,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (26, 2, 8,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (27, 2, 9,  true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (28, 2, 10, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (29, 2, 11, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (30, 2, 12, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (31, 2, 13, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (32, 2, 14, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (33, 2, 15, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (34, 2, 16, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (35, 2, 17, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (36, 2, 18, true, true, true, true, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),

        -- Supervisor (Role ID = 3)
        (37, 3, 1,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (38, 3, 2,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (39, 3, 3,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (40, 3, 4,  true,  true,  true,  true,  ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (41, 3, 5,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (42, 3, 6,  true,  true,  true,  true,  ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (43, 3, 7,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (44, 3, 8,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (45, 3, 9,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (46, 3, 10, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (47, 3, 11, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (48, 3, 12, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (49, 3, 13, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (50, 3, 14, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (51, 3, 15, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (52, 3, 16, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (53, 3, 17, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (54, 3, 18, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),

        -- Analyzer Operator (Role ID = 5)
        (55, 5, 1,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (56, 5, 2,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (57, 5, 3,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (58, 5, 4,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (59, 5, 5,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (60, 5, 6,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (61, 5, 7,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (62, 5, 8,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (63, 5, 9,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (64, 5, 10, true,  true,  true,  true,  ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (65, 5, 11, true,  true,  true,  true,  ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (66, 5, 12, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (67, 5, 13, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (68, 5, 14, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (69, 5, 15, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (70, 5, 16, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (71, 5, 17, true,  true,  true,  true,  ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (72, 5, 18, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),

        -- Pump Operator (Role ID = 6)
        (73, 6, 1,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (74, 6, 2,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (75, 6, 3,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (76, 6, 4,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (77, 6, 5,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (78, 6, 6,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (79, 6, 7,  true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (80, 6, 8,  false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (81, 6, 9,  true,  true,  true,  true,  ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (82, 6, 10, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (83, 6, 11, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (84, 6, 12, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (85, 6, 13, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (86, 6, 14, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (87, 6, 15, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (88, 6, 16, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (89, 6, 17, true,  false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE''),
        (90, 6, 18, false, false, false, false, ''2026-05-21 07:10:00'', 1, ''2026-05-21 07:10:00'', 1, null, null, ''ACTIVE'')
        ON CONFLICT (role_access_id) DO UPDATE SET 
            can_view = EXCLUDED.can_view, 
            can_add = EXCLUDED.can_add, 
            can_edit = EXCLUDED.can_edit, 
            can_delete = EXCLUDED.can_delete, 
            updated_at = EXCLUDED.updated_at, 
            updated_by = EXCLUDED.updated_by, 
            status = EXCLUDED.status';

    END IF;
END $$;