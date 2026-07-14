-- =========================================================================
-- BAGIAN 1: RESEED DATA ACCESS_MENUS & ROLE_ACCESS (DIPROTEKSI DENGAN IF)
-- =========================================================================
DO $$
BEGIN
    -- Hanya jalankan jika tabel 'access_menus' dan 'role_access' benar-benar ada
    IF EXISTS (
        SELECT FROM information_schema.tables 
        WHERE table_schema = 'public' AND table_name = 'access_menus'
    ) AND EXISTS (
        SELECT FROM information_schema.tables 
        WHERE table_schema = 'public' AND table_name = 'role_access'
    ) THEN

        -- 1. Bersihkan data lama di role_access
        DELETE FROM public.role_access;

        -- 2. Reseed data master access_menus
        INSERT INTO public.access_menus (menu_id, menu_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES 
        (1,  'USER', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (2,  'PARTICIPANT', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (3,  'PROTOCOLSAMPLINGSCHEDULE', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (4,  'SESSION', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (5,  'INFUSIONMONITORING', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (6,  'BLOODDRAW', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (7,  'PREPARATIONCHECK', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (8,  'GLOBALCONFIGURATION', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (9,  'PHASECONFIGURATION', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
        (10, 'ROLEACCESS', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE')
        ON CONFLICT (menu_id) DO UPDATE SET 
            menu_name = EXCLUDED.menu_name, 
            created_at = EXCLUDED.created_at, 
            created_by = EXCLUDED.created_by, 
            updated_at = EXCLUDED.updated_at, 
            updated_by = EXCLUDED.updated_by, 
            status = EXCLUDED.status;

        -- 3. Reseed data role_access baru (50 baris data)
        INSERT INTO public.role_access (can_add, can_delete, can_edit, can_view, created_by, deleted_by, menu_id, role_access_id, role_id, updated_by, created_at, deleted_at, updated_at, status) VALUES 
        (true,  true,  true,  true,  null, null, 10, 50, 2, null, '2026-07-08 09:47:34.349', null, '2026-07-08 09:47:42.373', 'ACTIVE'),
        (false, false, false, false, 1,    null, 10, 49, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 9,  48, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, false, 1,    null, 8,  47, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 7,  46, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 6,  45, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 5,  44, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 4,  43, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 3,  42, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 2,  41, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, false, 1,    null, 1,  40, 6, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, false, 1,    null, 10, 39, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 9,  38, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, false, 1,    null, 8,  37, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 7,  36, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 6,  35, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 5,  34, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 4,  33, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-07-08 09:48:57.346', 'ACTIVE'),
        (false, false, false, true,  1,    null, 3,  32, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 2,  31, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, false, 1,    null, 1,  30, 5, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, false, 1,    null, 10, 29, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-07-09 15:02:00.677', 'ACTIVE'),
        (false, false, false, true,  1,    null, 9,  28, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, false, 1,    null, 8,  27, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-07-09 15:02:47.663', 'ACTIVE'),
        (false, false, false, true,  1,    null, 7,  26, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 6,  25, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 5,  24, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 4,  23, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, true,  1,    null, 3,  22, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-07-10 10:35:01.637', 'ACTIVE'),
        (false, false, false, true,  1,    null, 2,  21, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (false, false, false, false, 1,    null, 1,  20, 3, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 9,  19, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 8,  18, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 7,  17, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 6,  16, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 5,  15, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 4,  14, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 3,  13, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 2,  12, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 1,  11, 2, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 10, 10, 1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 9,  9,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 8,  8,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 7,  7,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 6,  6,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 5,  5,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 4,  4,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 3,  3,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 2,  2,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE'),
        (true,  true,  true,  true,  1,    null, 1,  1,  1, 1,    '2026-05-21 07:10:00.000', null, '2026-05-21 07:10:00.000', 'ACTIVE')
        ON CONFLICT (role_access_id) DO UPDATE SET 
            can_add = EXCLUDED.can_add,
            can_delete = EXCLUDED.can_delete,
            can_edit = EXCLUDED.can_edit,
            can_view = EXCLUDED.can_view,
            updated_at = EXCLUDED.updated_at,
            updated_by = EXCLUDED.updated_by,
            status = EXCLUDED.status;

    END IF;
END $$;

-- =========================================================================
-- BAGIAN 2: AUTO ADJUST SEQUENCE DATABASE (DINAMIS)
-- =========================================================================
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT 
            tc.table_name, 
            kcu.column_name, 
            pg_get_serial_sequence(tc.table_schema || '.' || tc.table_name, kcu.column_name) AS seq_name
        FROM 
            information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu 
              ON tc.constraint_name = kcu.constraint_name
             AND tc.table_schema = kcu.table_schema
        WHERE 
            tc.constraint_type = 'PRIMARY KEY'
            AND tc.table_schema = 'public'
            AND pg_get_serial_sequence(tc.table_schema || '.' || tc.table_name, kcu.column_name) IS NOT NULL
    LOOP
        EXECUTE format('SELECT setval(%L, COALESCE((SELECT max(%I) FROM public.%I), 1))', 
            r.seq_name, r.column_name, r.table_name);
    END LOOP;
END $$;

-- =========================================================================
-- BAGIAN 3: SINKRONISASI SEQUENCE MANUAL (DIPROTEKSI DENGAN IF EXISTS)
-- =========================================================================
DO $$
BEGIN
    -- Pengecekan sequence: role_access_id_seq
    IF to_regclass('public.role_access_id_seq') IS NOT NULL THEN
        PERFORM setval('public.role_access_id_seq', COALESCE((SELECT max(role_access_id) FROM public.role_access), 1));
    END IF;
    
    -- Pengecekan sequence: role_id_seq
    IF to_regclass('public.role_id_seq') IS NOT NULL THEN
        PERFORM setval('public.role_id_seq', COALESCE((SELECT max(role_id) FROM public.roles), 1));
    END IF;
    
    -- Pengecekan sequence: user_id_seq
    IF to_regclass('public.user_id_seq') IS NOT NULL THEN
        PERFORM setval('public.user_id_seq', COALESCE((SELECT max(user_id) FROM public.users), 1));
    END IF;
    
    -- Pengecekan sequence: users_user_id_seq
    IF to_regclass('public.users_user_id_seq') IS NOT NULL THEN
        PERFORM setval('public.users_user_id_seq', COALESCE((SELECT max(user_id) FROM public.users), 1));
    END IF;
    
    -- Pengecekan sequence: phase_conf_id_seq
    IF to_regclass('public.phase_conf_id_seq') IS NOT NULL THEN
        PERFORM setval('public.phase_conf_id_seq', COALESCE((SELECT max(phase_conf_id) FROM public.phase_configurations), 1));
    END IF;
END $$;