package com.tujuhsembilan.glucoseclamp.config;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class SeedDataRunner implements CommandLineRunner {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // seedAccessMenus();
        // seedRoles();
        // seedRoleAccesses();
        // seedUsers();
        // seedParticipants();
        // seedProtocols();
        // seedPhaseConfigurations();
        // seedSessions();
        // seedSamplingSchedules();
        // seedGlobalConfigurations();
    }

    private void seedRoles() {
        batch(
                "INSERT INTO roles (role_id, role_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (role_id) DO UPDATE SET role_name = EXCLUDED.role_name, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, "Superadmin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, "Admin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, "Supervisor", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(4, "Operator Insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(5, "Operator Analyzer", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(6, "Operator Pump", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(7, "Operator Pump 2", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 6, "DELETED")
        );
    }

    private void seedAccessMenus() {
        batch(
            "INSERT INTO access_menus (menu_id, menu_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, status = EXCLUDED.status",
            row(1,  "USER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(2,  "ROLE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(3,  "PARTICIPANT", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(4,  "PROTOCOL", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(5,  "PHASECONFIGURATION", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(6,  "SAMPLINGSCHEDULE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(7,  "SESSION", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(8,  "SESSIONDEVICE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(9,  "INFUSIONMONITORING", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(10, "LABRESULT", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(11, "BLOODSAMPLE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(12, "VITALSIGN", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(13, "ANTHROPOMETRY", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(14, "ANAMNESIS", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(15, "DEVICE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(16, "GLOBALCONFIGURATION", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(17, "ACTIVITY", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(18, "ACCESSMENU", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }
    private void seedRoleAccesses() {
        batch(
            "INSERT INTO role_access (role_access_id, role_id, menu_id, can_view, can_add, can_edit, can_delete, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (role_access_id) DO UPDATE SET can_view = EXCLUDED.can_view, can_add = EXCLUDED.can_add, can_edit = EXCLUDED.can_edit, can_delete = EXCLUDED.can_delete, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, status = EXCLUDED.status",
            
            // ==========================================
            // 1. SUPERADMIN / SUPERUSER (ROLE ID = 1) -> TRUE untuk semua Menu
            // ==========================================
            row(1,  1, 1,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // USER
            row(2,  1, 2,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ROLE
            row(3,  1, 3,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PARTICIPANT
            row(4,  1, 4,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PROTOCOL
            row(5,  1, 5,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PHASECONFIGURATION
            row(6,  1, 6,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SAMPLINGSCHEDULE
            row(7,  1, 7,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSION
            row(8,  1, 8,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSIONDEVICE
            row(9,  1, 9,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // INFUSIONMONITORING
            row(10, 1, 10, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // LABRESULT
            row(11, 1, 11, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // BLOODSAMPLE
            row(12, 1, 12, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // VITALSIGN
            row(13, 1, 13, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANTHROPOMETRY
            row(14, 1, 14, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANAMNESIS
            row(15, 1, 15, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // DEVICE
            row(16, 1, 16, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // GLOBALCONFIGURATION
            row(17, 1, 17, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACTIVITY
            row(18, 1, 18, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACCESSMENU

            // ==========================================
            // 2. ADMIN (ROLE ID = 2) -> TRUE untuk semua Menu
            // ==========================================
            row(19, 2, 1,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // USER
            row(20, 2, 2,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ROLE
            row(21, 2, 3,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PARTICIPANT
            row(22, 2, 4,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PROTOCOL
            row(23, 2, 5,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PHASECONFIGURATION
            row(24, 2, 6,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SAMPLINGSCHEDULE
            row(25, 2, 7,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSION
            row(26, 2, 8,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSIONDEVICE
            row(27, 2, 9,  true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // INFUSIONMONITORING
            row(28, 2, 10, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // LABRESULT
            row(29, 2, 11, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // BLOODSAMPLE
            row(30, 2, 12, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // VITALSIGN
            row(31, 2, 13, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANTHROPOMETRY
            row(32, 2, 14, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANAMNESIS
            row(33, 2, 15, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // DEVICE
            row(34, 2, 16, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // GLOBALCONFIGURATION
            row(35, 2, 17, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACTIVITY
            row(36, 2, 18, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACCESSMENU

            // ==========================================
            // 3. SUPERVISOR (ROLE ID = 3) -> Add/Edit/Delete hanya di Protocol & Sampling
            // ==========================================
            row(37, 3, 1,  false, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // USER (Semua FALSE)
            row(38, 3, 2,  false, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ROLE (Semua FALSE)
            row(39, 3, 3,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PARTICIPANT (View Only)
            row(40, 3, 4,  true,  true,  true,  true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PROTOCOL (Full)
            row(41, 3, 5,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PHASECONFIGURATION (View Only)
            row(42, 3, 6,  true,  true,  true,  true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SAMPLINGSCHEDULE (Full)
            row(43, 3, 7,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSION (View Only)
            row(44, 3, 8,  false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSIONDEVICE (View Only)
            row(45, 3, 9,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // INFUSIONMONITORING (View Only)
            row(46, 3, 10, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // LABRESULT (View Only)
            row(47, 3, 11, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // BLOODSAMPLE (View Only)
            row(48, 3, 12, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // VITALSIGN (View Only)
            row(49, 3, 13, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANTHROPOMETRY (View Only)
            row(50, 3, 14, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANAMNESIS (View Only)
            row(51, 3, 15, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // DEVICE (View Only)
            row(52, 3, 16, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // GLOBALCONFIGURATION (View Only)
            row(53, 3, 17, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACTIVITY (View Only)
            row(54, 3, 18, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACCESSMENU (View Only)

            // ==========================================
            // 4. ANALYZER OPERATOR (ROLE ID = 5) -> Add/Edit/Delete hanya di Activity
            // ==========================================
            row(55, 5, 1,  false, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // USER (Semua FALSE)
            row(56, 5, 2,  false, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ROLE (Semua FALSE)
            row(57, 5, 3,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PARTICIPANT (View Only)
            row(58, 5, 4,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PROTOCOL (View Only)
            row(59, 5, 5,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PHASECONFIGURATION (View Only)
            row(60, 5, 6,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SAMPLINGSCHEDULE (View Only)
            row(61, 5, 7,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSION (View Only)
            row(62, 5, 8,  false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSIONDEVICE (View Only)
            row(63, 5, 9,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // INFUSIONMONITORING (View Only)
            row(64, 5, 10, true,  true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // LABRESULT (View Only)
            row(65, 5, 11, true,  true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // BLOODSAMPLE (View Only)
            row(66, 5, 12, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // VITALSIGN (View Only)
            row(67, 5, 13, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANTHROPOMETRY (View Only)
            row(68, 5, 14, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANAMNESIS (View Only)
            row(69, 5, 15, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // DEVICE (View Only)
            row(70, 5, 16, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // GLOBALCONFIGURATION (View Only)
            row(71, 5, 17, true,  true,  true,  true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACTIVITY (Full)
            row(72, 5, 18, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACCESSMENU (View Only)

            // ==========================================
            // 5. PUMP OPERATOR (ROLE ID = 6) -> Add/Edit/Delete hanya di Infusion Monitoring
            // ==========================================
            row(73, 6, 1,  false, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // USER (Semua FALSE)
            row(74, 6, 2,  false, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ROLE (Semua FALSE)
            row(75, 6, 3,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PARTICIPANT (View Only)
            row(76, 6, 4,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PROTOCOL (View Only)
            row(77, 6, 5,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PHASECONFIGURATION (View Only)
            row(78, 6, 6,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SAMPLINGSCHEDULE (View Only)
            row(79, 6, 7,  true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSION (View Only)
            row(80, 6, 8,  false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSIONDEVICE (View Only)
            row(81, 6, 9,  true,  true,  true,  true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // INFUSIONMONITORING (Full)
            row(82, 6, 10, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // LABRESULT (View Only)
            row(83, 6, 11, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // BLOODSAMPLE (View Only)
            row(84, 6, 12, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // VITALSIGN (View Only)
            row(85, 6, 13, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANTHROPOMETRY (View Only)
            row(86, 6, 14, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANAMNESIS (View Only)
            row(87, 6, 15, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // DEVICE (View Only)
            row(88, 6, 16, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // GLOBALCONFIGURATION (View Only)
            row(89, 6, 17, true,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ACTIVITY (View Only)
            row(90, 6, 18, false,  false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")  // ACCESSMENU (View Only)
        );
    }

    private void seedUsers() {
        String password = passwordEncoder.encode("hash123");
        batch(
                "INSERT INTO users (user_id, role_id, position_name, name, username, email, password, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET role_id = EXCLUDED.role_id, position_name = EXCLUDED.position_name, name = EXCLUDED.name, username = EXCLUDED.username, email = EXCLUDED.email, password = EXCLUDED.password, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, 1, "SUPER ADMIN", "Super Admin", "superadmin", "superadmin@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, 2, "ADMINISTRASI", "Admin RSCM", "admin", "admin@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, 3, "DOKTER PENELITI", "Dr Budi", "budi", "budi@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(4, 4, "PERAWAT/DOKTER", "Fitri", "fitri", "fitri@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(5, 5, "ANALIS", "Rina", "rina", "rina@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(6, 6, "PERAWAT", "Agus", "agus", "agus@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(7, 6, "PERAWAT", "Bagas", "bagas", "bagas@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "INACTIVE")
        );
        jdbcTemplate.update("UPDATE users SET password = ? WHERE password = ?", password, "PASSWORD_PLACEHOLDER");
    }

    private void seedSessions() {
    batch(
        "INSERT INTO sessions (session_id, participant_id, protocol_id, visit_date, start_time, end_time, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (session_id) DO UPDATE SET participant_id = EXCLUDED.participant_id, protocol_id = EXCLUDED.protocol_id, visit_date = EXCLUDED.visit_date, start_time = EXCLUDED.start_time, end_time = EXCLUDED.end_time, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
        row(101, "PAT-001", 1, ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
    );
}

    private void seedActivities() {
        batch(
                "INSERT INTO activities (activity_id, session_id, actor_id, time, activity_type, activity_desc, activity_status, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (activity_id) DO UPDATE SET session_id = EXCLUDED.session_id, actor_id = EXCLUDED.actor_id, time = EXCLUDED.time, activity_type = EXCLUDED.activity_type, activity_desc = EXCLUDED.activity_desc, activity_status = EXCLUDED.activity_status, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, 101, 3, ts("2026-05-21 08:00:00"), "BLOOD_DRAW", "T-30", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, 101, 3, ts("2026-05-21 08:00:00"), "PK_SAMPLE_COLLECTION", "PK-C 1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, 101, 3, ts("2026-05-21 08:10:00"), "BLOOD_DRAW", "T-20", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(4, 101, 3, ts("2026-05-21 08:20:00"), "BLOOD_DRAW", "T-10", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(5, 101, 3, ts("2026-05-21 08:30:00"), "BLOOD_DRAW", "T-0", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(6, 101, 3, ts("2026-05-21 08:30:00"), "INSULIN_INJECTION", "SC", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(7, 101, 3, ts("2026-05-21 08:40:00"), "BLOOD_DRAW", "GD1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(8, 101, 3, ts("2026-05-21 08:50:00"), "BLOOD_DRAW", "GD2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(9, 101, 3, ts("2026-05-21 09:00:00"), "BLOOD_DRAW", "GD3", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(10, 101, 3, ts("2026-05-21 09:00:00"), "PK_SAMPLE_COLLECTION", "PK-C 2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(11, 101, 3, ts("2026-05-21 08:00:00"), "BLOOD_DRAW", "T-30", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(12, 101, 3, ts("2026-05-21 08:00:00"), "PK_SAMPLE_COLLECTION", "PK-C 1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(13, 101, 3, ts("2026-05-21 08:10:00"), "BLOOD_DRAW", "T-20", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(14, 101, 3, ts("2026-05-21 08:20:00"), "BLOOD_DRAW", "T-10", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(15, 101, 3, ts("2026-05-21 08:30:00"), "BLOOD_DRAW", "T-0", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(16, 101, 3, ts("2026-05-21 08:30:00"), "INSULIN_INJECTION", "SC", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(17, 101, 3, ts("2026-05-21 08:40:00"), "BLOOD_DRAW", "GD1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(18, 101, 3, ts("2026-05-21 08:50:00"), "BLOOD_DRAW", "GD2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(19, 101, 3, ts("2026-05-21 09:00:00"), "BLOOD_DRAW", "GD3", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(20, 101, 3, ts("2026-05-21 09:00:00"), "PK_SAMPLE_COLLECTION", "PK-C 2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(21, 101, 3, ts("2026-05-21 09:10:00"), "BLOOD_DRAW", "GD4", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(22, 101, 3, ts("2026-05-21 09:20:00"), "BLOOD_DRAW", "GD5", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }

    private void seedDevices() {
        batch(
                "INSERT INTO devices (device_id, device_type, device_brand, serial_number, last_calibration_date, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (device_id) DO UPDATE SET device_type = EXCLUDED.device_type, device_brand = EXCLUDED.device_brand, serial_number = EXCLUDED.serial_number, last_calibration_date = EXCLUDED.last_calibration_date, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, "Infusion Pump", "B. Braun Infusomat", "SN-991823", ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, "Chemistry Analyzer", "Cobas C111", "SN-774122", ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, "Infusion Pump", "Terumo TE-112", "SN-110099", ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }

    private void seedParticipants() {
        batch(
                "INSERT INTO participants (participant_id, medical_record_no, name, gender, dob, number_phone, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (participant_id) DO UPDATE SET medical_record_no = EXCLUDED.medical_record_no, name = EXCLUDED.name, gender = EXCLUDED.gender, dob = EXCLUDED.dob, number_phone = EXCLUDED.number_phone, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row("PAT-001", "MR889100", "Adrian Saputra", "Male", d("1998-06-10"), "8123456789", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }

    private void seedProtocols() {
        batch(
            "INSERT INTO protocols (" +
            "protocol_code, protocol_name, " +
            "insulin_dose_rule, insulin_dose_unit, " +
            "glucose_target_min, glucose_target_max, " +
            "glucose_target_min_extreme, glucose_target_max_extreme, " +
            "glucose_target_unit, duration_hours, " +
            "glucose_drop_trigger_percentage, initial_glucose_infusion_rate, initial_glucose_infusion_rate_unit, " + // Kolom baru
            "version, " +
            "created_at, created_by, updated_at, updated_by, " +
            "deleted_at, deleted_by, status" +
            ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) " + // Total 21 parameter (tambah 3 tanda tanya)
            "ON CONFLICT (protocol_code) DO UPDATE SET " +
            "protocol_code = EXCLUDED.protocol_code, " +
            "protocol_name = EXCLUDED.protocol_name, " +
            "insulin_dose_rule = EXCLUDED.insulin_dose_rule, " +
            "insulin_dose_unit = EXCLUDED.insulin_dose_unit, " +
            "glucose_target_min = EXCLUDED.glucose_target_min, " +
            "glucose_target_max = EXCLUDED.glucose_target_max, " +
            "glucose_target_min_extreme = EXCLUDED.glucose_target_min_extreme, " +
            "glucose_target_max_extreme = EXCLUDED.glucose_target_max_extreme, " +
            "glucose_target_unit = EXCLUDED.glucose_target_unit, " +
            "duration_hours = EXCLUDED.duration_hours, " +
            "glucose_drop_trigger_percentage = EXCLUDED.glucose_drop_trigger_percentage, " + // Pembaruan konflik baru
            "initial_glucose_infusion_rate = EXCLUDED.initial_glucose_infusion_rate, " +     // Pembaruan konflik baru
            "initial_glucose_infusion_rate_unit = EXCLUDED.initial_glucose_infusion_rate_unit, " + // Pembaruan konflik baru
            "version = EXCLUDED.version, " +
            "created_at = EXCLUDED.created_at, " +
            "created_by = EXCLUDED.created_by, " +
            "updated_at = EXCLUDED.updated_at, " +
            "updated_by = EXCLUDED.updated_by, " +
            "deleted_at = EXCLUDED.deleted_at, " +
            "deleted_by = EXCLUDED.deleted_by, " +
            "status = EXCLUDED.status",

            row(
                "EGC001",
                "Euglycemic Clamp",
                "0.5",
                "U/kgBW SC",
                bd("90"),      // glucose_target_min
                bd("100"),     // glucose_target_max
                bd("80"),      // glucose_target_min_extreme
                bd("110"),     // glucose_target_max_extreme
                "mg/dL",
                bd("24"),
                bd("10"),      // glucose_drop_trigger_percentage (Nilai baru: 10%)
                bd("2"),       // initial_glucose_infusion_rate (Nilai baru: 2)
                "mg/kgBB/min",   // initial_glucose_infusion_rate_unit (Nilai baru)
                1.0f,
                ts("2026-05-21 07:10:00"),
                1,
                ts("2026-05-21 07:10:00"),
                1,
                null,
                null,
                "ACTIVE"
            )
        );
    }

    private void seedPhaseConfigurations() {
        batch(
                "INSERT INTO phase_configurations (phase_conf_id, phase_conf_priority, phase_conf_code, phase_conf_name, phase_conf_type, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (phase_conf_id) DO UPDATE SET phase_conf_priority = EXCLUDED.phase_conf_priority, phase_conf_code = EXCLUDED.phase_conf_code, phase_conf_name = EXCLUDED.phase_conf_name, phase_conf_type = EXCLUDED.phase_conf_type, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, 1, "PREP1", "Pemeriksaan Awal", "preparation", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, 2, "PREP2", "Pra - Tindakan", "preparation", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, 3, "BASE", "Baseline", "pre-insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(4, 4, "PH1", "Phase 1", "post-insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(5, 5, "PH2", "Phase 2", "post-insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(6, 6, "PH3", "Phase 3", "post-insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(7, 7, "FINAL", "Pemeriksaan Akhir", "finalization", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }


    private void seedSamplingSchedules() {
        batch(
                "INSERT INTO sampling_schedules (" +
                "schedule_code, protocol_id, phase_code, phase_name, phase_type, relative_minute, time_interval, " +
                "blood_raw, insulin_inject, pk_sample_collection, created_at, created_by, " +
                "updated_at, updated_by, deleted_at, deleted_by, status" +
                ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) " +
                "ON CONFLICT DO NOTHING",
                
                // PREPARATION (PREP1 & PREP2)
                row("PREP1", 1, "PREP1", "Pemeriksaan Awal", "preparation", 0, 30, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("PREP2", 1, "PREP2", "Pra - Tindakan", "preparation", 30, 30, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            
                // BASELINE (Langkah interval masing-masing 10m dimulai dari menit ke-60, berujung di T-00 saat injeksi insulin)
                row("T-30", 1, "BASE", "Baseline", "pre-insulin", 60, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("T-20", 1, "BASE", "Baseline", "pre-insulin", 70, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("T-10", 1, "BASE", "Baseline", "pre-insulin", 80, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("T-00", 1, "BASE", "Baseline", "pre-insulin", 90, 10, true, true, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                
                // PHASE 1 (GD-01 s.d GD-48 - Interval 10m, dimulai dari menit ke-100 s.d 570)
                row("GD-01", 1, "PH1", "Phase 1", "post-insulin", 100, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-02", 1, "PH1", "Phase 1", "post-insulin", 110, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-03", 1, "PH1", "Phase 1", "post-insulin", 120, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C2
                row("GD-04", 1, "PH1", "Phase 1", "post-insulin", 130, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-05", 1, "PH1", "Phase 1", "post-insulin", 140, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-06", 1, "PH1", "Phase 1", "post-insulin", 150, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C3
                row("GD-07", 1, "PH1", "Phase 1", "post-insulin", 160, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-08", 1, "PH1", "Phase 1", "post-insulin", 170, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-09", 1, "PH1", "Phase 1", "post-insulin", 180, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-10", 1, "PH1", "Phase 1", "post-insulin", 190, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-11", 1, "PH1", "Phase 1", "post-insulin", 200, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-12", 1, "PH1", "Phase 1", "post-insulin", 210, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C4
                row("GD-13", 1, "PH1", "Phase 1", "post-insulin", 220, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-14", 1, "PH1", "Phase 1", "post-insulin", 230, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-15", 1, "PH1", "Phase 1", "post-insulin", 240, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-16", 1, "PH1", "Phase 1", "post-insulin", 250, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-17", 1, "PH1", "Phase 1", "post-insulin", 260, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-18", 1, "PH1", "Phase 1", "post-insulin", 270, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-19", 1, "PH1", "Phase 1", "post-insulin", 280, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-20", 1, "PH1", "Phase 1", "post-insulin", 290, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-21", 1, "PH1", "Phase 1", "post-insulin", 300, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-22", 1, "PH1", "Phase 1", "post-insulin", 310, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-23", 1, "PH1", "Phase 1", "post-insulin", 320, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-24", 1, "PH1", "Phase 1", "post-insulin", 330, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C5
                row("GD-25", 1, "PH1", "Phase 1", "post-insulin", 340, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-26", 1, "PH1", "Phase 1", "post-insulin", 350, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-27", 1, "PH1", "Phase 1", "post-insulin", 360, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-28", 1, "PH1", "Phase 1", "post-insulin", 370, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-29", 1, "PH1", "Phase 1", "post-insulin", 380, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-30", 1, "PH1", "Phase 1", "post-insulin", 390, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-31", 1, "PH1", "Phase 1", "post-insulin", 400, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-32", 1, "PH1", "Phase 1", "post-insulin", 410, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-33", 1, "PH1", "Phase 1", "post-insulin", 420, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-34", 1, "PH1", "Phase 1", "post-insulin", 430, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-35", 1, "PH1", "Phase 1", "post-insulin", 440, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-36", 1, "PH1", "Phase 1", "post-insulin", 450, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C6
                row("GD-37", 1, "PH1", "Phase 1", "post-insulin", 460, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-38", 1, "PH1", "Phase 1", "post-insulin", 470, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-39", 1, "PH1", "Phase 1", "post-insulin", 480, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-40", 1, "PH1", "Phase 1", "post-insulin", 490, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-41", 1, "PH1", "Phase 1", "post-insulin", 500, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-42", 1, "PH1", "Phase 1", "post-insulin", 510, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-43", 1, "PH1", "Phase 1", "post-insulin", 520, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-44", 1, "PH1", "Phase 1", "post-insulin", 530, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-45", 1, "PH1", "Phase 1", "post-insulin", 540, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-46", 1, "PH1", "Phase 1", "post-insulin", 550, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-47", 1, "PH1", "Phase 1", "post-insulin", 560, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-48", 1, "PH1", "Phase 1", "post-insulin", 570, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                
                // PHASE 2 (GD-49 s.d GD-66 - tipe 'post-insulin' PH2, dimulai dari menit ke-590 s.d 930)
                row("GD-49", 1, "PH2", "Phase 2", "post-insulin", 590, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-50", 1, "PH2", "Phase 2", "post-insulin", 610, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-51", 1, "PH2", "Phase 2", "post-insulin", 630, 20, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C7
                row("GD-52", 1, "PH2", "Phase 2", "post-insulin", 650, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-53", 1, "PH2", "Phase 2", "post-insulin", 670, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-54", 1, "PH2", "Phase 2", "post-insulin", 690, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-55", 1, "PH2", "Phase 2", "post-insulin", 710, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-56", 1, "PH2", "Phase 2", "post-insulin", 730, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-57", 1, "PH2", "Phase 2", "post-insulin", 750, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-58", 1, "PH2", "Phase 2", "post-insulin", 770, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-59", 1, "PH2", "Phase 2", "post-insulin", 790, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-60", 1, "PH2", "Phase 2", "post-insulin", 810, 20, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C8
                row("GD-61", 1, "PH2", "Phase 2", "post-insulin", 830, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-62", 1, "PH2", "Phase 2", "post-insulin", 850, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-63", 1, "PH2", "Phase 2", "post-insulin", 870, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-64", 1, "PH2", "Phase 2", "post-insulin", 890, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-65", 1, "PH2", "Phase 2", "post-insulin", 910, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-66", 1, "PH2", "Phase 2", "post-insulin", 930, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                
                // PHASE 3 (GD-67 s.d GD-86 - tipe 'post-insulin' PH3, dimulai dari menit ke-960 s.d 1530)
                row("GD-67", 1, "PH3", "Phase 3", "post-insulin", 960, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-68", 1, "PH3", "Phase 3", "post-insulin", 990, 30, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C9
                row("GD-69", 1, "PH3", "Phase 3", "post-insulin", 1020, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-70", 1, "PH3", "Phase 3", "post-insulin", 1050, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-71", 1, "PH3", "Phase 3", "post-insulin", 1080, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-72", 1, "PH3", "Phase 3", "post-insulin", 1110, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-73", 1, "PH3", "Phase 3", "post-insulin", 1140, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-74", 1, "PH3", "Phase 3", "post-insulin", 1170, 30, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C10
                row("GD-75", 1, "PH3", "Phase 3", "post-insulin", 1200, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-76", 1, "PH3", "Phase 3", "post-insulin", 1230, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-77", 1, "PH3", "Phase 3", "post-insulin", 1260, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-78", 1, "PH3", "Phase 3", "post-insulin", 1290, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-79", 1, "PH3", "Phase 3", "post-insulin", 1320, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-80", 1, "PH3", "Phase 3", "post-insulin", 1350, 30, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C11
                row("GD-81", 1, "PH3", "Phase 3", "post-insulin", 1380, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-82", 1, "PH3", "Phase 3", "post-insulin", 1410, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-83", 1, "PH3", "Phase 3", "post-insulin", 1440, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-84", 1, "PH3", "Phase 3", "post-insulin", 1470, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-85", 1, "PH3", "Phase 3", "post-insulin", 1500, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("GD-86", 1, "PH3", "Phase 3", "post-insulin", 1530, 30, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PK-C12
                
                // FINALIZATION (FINAL - menit 1560)
                row("FINAL", 1, "FINAL", "Pemeriksaan Akhir", "finalization", 1560, 30, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }

    private void seedGlobalConfigurations() {
        batch(
                "INSERT INTO global_configurations (gconf_id, gconf_code, gconf_title, gconf_desc, gconf_value, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (gconf_id) DO UPDATE SET " +
                "gconf_code = EXCLUDED.gconf_code, " +
                "gconf_title = EXCLUDED.gconf_title, " +
                "gconf_desc = EXCLUDED.gconf_desc, " +
                "gconf_value = EXCLUDED.gconf_value, " +
                "updated_at = EXCLUDED.updated_at, " +
                "updated_by = EXCLUDED.updated_by, " +
                "status = EXCLUDED.status",
                row(
                    1, 
                    "ACTIVITY_CONFIRMATION_TIMER", 
                    "Activity Confirmation Timer (seconds)", 
                    "Configure the countdown duration before an activity can be confirmed.", 
                    "60", 
                    ts("2026-05-21 07:10:00"), 
                    1, 
                    ts("2026-05-21 07:10:00"), 
                    1, 
                    null, 
                    null, 
                    "ACTIVE"
                ),
                row(
                    2, 
                    "INITIAL_GLUCOSE_DROP_BASELINE", 
                    "Initial Glucose Drop (Baseline in %)", 
                    "Configure the percentage threshold of glucose level drop from the baseline to trigger initial tracking or alerts.", 
                    "10", 
                    ts("2026-05-21 07:10:00"), 
                    1, 
                    ts("2026-05-21 07:10:00"), 
                    1, 
                    null, 
                    null, 
                    "ACTIVE"
                )
        );
    }

    private void seedBloodSamples() {
        batch(
            "INSERT INTO blood_samples (blood_sample_id, activity_id, collected_by, sample_time, sample_type, tube_type, volume_ml, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (blood_sample_id) DO UPDATE SET activity_id = EXCLUDED.activity_id, collected_by = EXCLUDED.collected_by, sample_time = EXCLUDED.sample_time, sample_type = EXCLUDED.sample_type, tube_type = EXCLUDED.tube_type, volume_ml = EXCLUDED.volume_ml, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
            row("BS-001", "ACT-001", 4, ts("2026-05-21 08:00:05"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-002", "ACT-002", 4, ts("2026-05-21 08:00:05"), "C-Peptide", "EDTA", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-003", "ACT-003", 4, ts("2026-05-21 08:10:02"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-004", "ACT-004", 4, ts("2026-05-21 08:20:10"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-005", "ACT-005", 4, ts("2026-05-21 08:30:10"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-006", "ACT-007", 4, ts("2026-05-21 08:40:05"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-007", "ACT-008", 4, ts("2026-05-21 08:50:00"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-008", "ACT-009", 4, ts("2026-05-21 09:00:15"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-009", "ACT-010", 4, ts("2026-05-21 09:00:15"), "C-Peptide", "EDTA", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-010", "ACT-011", 4, ts("2026-05-21 09:10:00"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-011", "ACT-012", 4, ts("2026-05-21 09:20:00"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-012", "ACT-012", 4, ts("2026-05-21 09:20:00"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE")
        );
    }

    private void seedLabResults() {
        batch(
            "INSERT INTO lab_results (lab_result_id, blood_sample_id, parameter_name, verified_by, value, reference_range_min, reference_range_max, unit, abnormal_flag, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (lab_result_id) DO UPDATE SET blood_sample_id = EXCLUDED.blood_sample_id, parameter_name = EXCLUDED.parameter_name, verified_by = EXCLUDED.verified_by, value = EXCLUDED.value, reference_range_min = EXCLUDED.reference_range_min, reference_range_max = EXCLUDED.reference_range_max, unit = EXCLUDED.unit, abnormal_flag = EXCLUDED.abnormal_flag, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
            row("LAB-001", "BS-001", "Glucose", 4, bd("95"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-002", "BS-002", "Insulin", 4, bd("12.1"), bd("2.6"), bd("24.9"), "ulU/ml", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-003", "BS-003", "C-Peptide", 4, bd("1.5"), bd("0.5"), bd("2.7"), "ng/mL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-004", "BS-004", "Glucose", 4, bd("94"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-005", "BS-005", "Glucose", 4, bd("95.5"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-006", "BS-006", "Glucose", 4, bd("95"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-007", "BS-007", "Glucose", 4, bd("88"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-008", "BS-008", "Glucose", 4, bd("85"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-009", "BS-009", "Glucose", 4, bd("82.5"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-010", "BS-010", "Insulin", 4, bd("12.1"), bd("2.6"), bd("24.9"), "ulU/ml", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-011", "BS-011", "C-Peptide", 4, bd("4.8"), bd("0.5"), bd("2.7"), "ng/mL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE")
        );
    }

    private void syncSequences() {
        jdbcTemplate.execute("SELECT setval('role_id_seq', COALESCE((SELECT MAX(role_id) FROM roles), 0) + 1, false)");
        jdbcTemplate.execute("SELECT setval('user_id_seq', COALESCE((SELECT MAX(user_id) FROM users), 0) + 1, false)");
        jdbcTemplate.execute("SELECT setval('device_id_seq', COALESCE((SELECT MAX(device_id) FROM devices), 0) + 1, false)");
        jdbcTemplate.execute("SELECT setval('phase_conf_id_seq', COALESCE((SELECT MAX(phase_conf_id) FROM phase_configurations), 0) + 1, false)");
        jdbcTemplate.execute("SELECT setval('global_config_id_seq', COALESCE((SELECT MAX(gconf_id) FROM global_configurations), 0) + 1, false)");
    }

    private void batch(String sql, Object[]... rows) {
        jdbcTemplate.batchUpdate(sql, Arrays.asList(rows));
    }

    private static Object[] row(Object... values) {
        return values;
    }

    private static Timestamp ts(String value) {
        return Timestamp.valueOf(LocalDateTime.parse(value, DATE_TIME_FORMATTER));
    }

    private static LocalDate d(String value) {
        return LocalDate.parse(value);
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}