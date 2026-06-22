package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.ActivityRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.ActivityStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.ActivityUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ActivityResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.exception.classes.DataNotFoundException;
import com.tujuhsembilan.glucoseclamp.model.Activity;
import com.tujuhsembilan.glucoseclamp.model.SamplingSchedule;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import com.tujuhsembilan.glucoseclamp.repository.ActivityRepository;
import com.tujuhsembilan.glucoseclamp.repository.SamplingScheduleRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import com.tujuhsembilan.glucoseclamp.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final SessionRepository sessionRepository;
    private final SamplingScheduleRepository samplingScheduleRepository;
    private final CurrentUserService currentUserService;

    public ApiDataResponseBuilder getAllActivities(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<ActivityResponse> result = activityRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data activity")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getActivityById(String activityId) {
        Activity activity = activityRepository.findByIdAndDeletedAtIsNull(activityId).orElse(null);
        if (activity == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data activity tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(activity))
                .message("Berhasil mendapatkan data activity")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getActivitiesBySession(Integer sessionId) {
        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId)
            .orElseThrow(() -> new DataNotFoundException("Session tidak ditemukan"));

        List<ActivityResponse> result = activityRepository.findBySessionIdAndDeletedAtIsNull(session.getSessionId())
            .stream()
            .sorted(Comparator.comparing(Activity::getTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Activity::getActivityId))
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data activity berdasarkan session")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addActivity(ActivityRequest request) {
        Session session = sessionRepository.findByIdAndDeletedAtIsNull(request.getSessionId())
                .orElseThrow(() -> new DataNotFoundException("Session tidak ditemukan"));

        Activity activity = new Activity();
        activity.setActivityId(buildActivityId(nextSequence(), request.getActivityType(), session.getSessionId()));
        activity.setSession(session);
        activity.setActor(currentUserService.getCurrentUserEntity());
        activity.setTime(normalizeToSeconds(request.getTime()));
        activity.setActivityType(request.getActivityType());
        activity.setActivityDesc(request.getActivityDesc());
        activity.setActivityStatus(request.getActivityStatus() == null ? ActivityStatus.INQUEUE : request.getActivityStatus());
        activity.setStatus(EntityStatus.ACTIVE);
        activity.setCreatedBy(currentUserService.getCurrentUserId());
        activity.setUpdatedBy(currentUserService.getCurrentUserId());

        activityRepository.save(activity);
        syncSessionStatus(session);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(activity))
                .message("Activity berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateActivity(String activityId, ActivityUpdateRequest request) {
        Activity activity = requireActiveActivity(activityId);

        if (request.getTime() != null) {
            activity.setTime(normalizeToSeconds(request.getTime()));
        }
        if (request.getActivityType() != null && !request.getActivityType().isBlank()) {
            activity.setActivityType(request.getActivityType().trim());
        }
        if (request.getActivityDesc() != null && !request.getActivityDesc().isBlank()) {
            activity.setActivityDesc(request.getActivityDesc().trim());
        }
        if (request.getActivityStatus() != null) {
            activity.setActivityStatus(request.getActivityStatus());
        }

        activity.setUpdatedBy(currentUserService.getCurrentUserId());
        activityRepository.save(activity);
        syncSessionStatus(activity.getSession());

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(activity))
                .message("Activity berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateActivityStatus(String activityId, ActivityStatusUpdateRequest request) {
        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Activity activity = requireActiveActivity(activityId);
        activity.setStatus(request.getStatus());
        activity.setUpdatedBy(currentUserService.getCurrentUserId());
        activityRepository.save(activity);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(activity))
                .message("Status activity berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteActivity(String activityId) {
        Activity activity = requireActiveActivity(activityId);
        Integer currentUserId = currentUserService.getCurrentUserId();

        activity.setDeletedAt(LocalDateTime.now());
        activity.setDeletedBy(currentUserId);
        activity.setStatus(EntityStatus.DELETED);
        activity.setUpdatedBy(currentUserId);

        activityRepository.save(activity);
        syncSessionStatus(activity.getSession());

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(activity))
                .message("Activity berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public List<Activity> generateActivitiesForSession(Session session, User actor, Integer actorId) {
        List<SamplingSchedule> samplingSchedules = loadSamplingSchedules(session.getProtocol().getProtocolId());
        ActivityGenerationState generationState = buildActivityGenerationState(samplingSchedules);

        List<Activity> activities = new ArrayList<>();
        for (SamplingSchedule detail : samplingSchedules) {
            activities.addAll(buildActivitiesForDetail(session, actor, actorId, detail, generationState));
        }

        activityRepository.saveAll(activities);
        return activities;
    }

    @Transactional
    public void saveActivities(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            return;
        }
        activityRepository.saveAll(activities);
    }

    @Transactional(readOnly = true)
    public List<Activity> findActiveActivitiesForSession(Integer sessionId) {
        return activityRepository.findBySessionIdAndDeletedAtIsNull(sessionId);
    }

    @Transactional
    public void softDeleteActivitiesForSession(Integer sessionId, Integer actorId) {
        List<Activity> existingActivities = activityRepository.findBySessionIdAndDeletedAtIsNull(sessionId);
        for (Activity activity : existingActivities) {
            activity.setDeletedAt(LocalDateTime.now());
            activity.setDeletedBy(actorId);
            activity.setStatus(EntityStatus.DELETED);
            activity.setUpdatedBy(actorId);
        }
        activityRepository.saveAll(existingActivities);
    }

    @Transactional
    public void updateActivityStatusForSession(Integer sessionId, EntityStatus status, Integer actorId) {
        List<Activity> existingActivities = activityRepository.findBySessionIdAndDeletedAtIsNull(sessionId);
        for (Activity activity : existingActivities) {
            activity.setStatus(status);
            activity.setUpdatedBy(actorId);
        }
        activityRepository.saveAll(existingActivities);
    }

    @Transactional
    public List<Activity> shiftPendingActivitiesForSession(Integer sessionId, Duration delta, Integer actorId) {
        List<Activity> activitiesToUpdate = activityRepository.findBySessionIdAndDeletedAtIsNullAndNotCompleted(sessionId);
        for (Activity activity : activitiesToUpdate) {
            if (activity.getTime() != null) {
                activity.setTime(normalizeToSeconds(activity.getTime().plus(delta)));
                activity.setUpdatedBy(actorId);
            }
        }
        activityRepository.saveAll(activitiesToUpdate);
        return activitiesToUpdate;
    }

    private Activity requireActiveActivity(String activityId) {
        Activity activity = activityRepository.findByIdAndDeletedAtIsNull(activityId).orElse(null);
        if (activity == null) {
            throw new DataNotFoundException("Activity tidak ditemukan");
        }
        return activity;
    }

    private void syncSessionStatus(Session session) {
        if (session.getSessionStatus() == SessionStatus.COMPLETED) {
            return;
        }
        long totalActivities = activityRepository.findBySessionIdAndDeletedAtIsNull(session.getSessionId()).size();
        long completedActivities = activityRepository.findBySessionIdAndDeletedAtIsNull(session.getSessionId())
                .stream()
                .filter(activity -> activity.getActivityStatus() == ActivityStatus.COMPLETED)
                .count();

        if (completedActivities == 0) {
            session.setSessionStatus(SessionStatus.PREP);
        } else if (completedActivities < totalActivities) {
            session.setSessionStatus(SessionStatus.RUNNING);
        } else if (totalActivities > 0) {
            session.setSessionStatus(SessionStatus.COMPLETED);
        }

        session.setUpdatedBy(currentUserService.getCurrentUserId());
        sessionRepository.save(session);
    }

    private ActivityResponse mapToResponse(Activity activity) {
        Integer calculatedRelativeMinute = null;
        
        // Menghitung selisih menit secara otomatis di memori (runtime Java)
        if (activity.getSession() != null && activity.getSession().getStartTime() != null && activity.getTime() != null) {
            calculatedRelativeMinute = (int) java.time.Duration.between(
                activity.getSession().getStartTime(), 
                activity.getTime()
            ).toMinutes();
        }

        return ActivityResponse.builder()
                .activityId(activity.getActivityId())
                .sessionId(activity.getSession() == null ? null : activity.getSession().getSessionId())
                .actorId(activity.getActor() == null ? null : activity.getActor().getUserId())
                .time(activity.getTime())
                
                // Mengirim hasil kalkulasi dinamis ke frontend
                .relativeMinute(calculatedRelativeMinute) 
                
                .activityType(activity.getActivityType())
                .activityDesc(activity.getActivityDesc())
                .activityStatus(activity.getActivityStatus())
                .status(activity.getStatus())
                .createdAt(activity.getCreatedAt())
                .createdBy(activity.getCreatedBy())
                .updatedAt(activity.getUpdatedAt())
                .updatedBy(activity.getUpdatedBy())
                .deletedAt(activity.getDeletedAt())
                .deletedBy(activity.getDeletedBy())
                .build();
    }

    private LocalDateTime normalizeToSeconds(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.truncatedTo(ChronoUnit.SECONDS);
    }

    private int nextSequence() {
        try {
            var lastOpt = activityRepository.findTopByDeletedAtIsNullOrderByActivityIdDesc();
            if (lastOpt.isPresent()) {
                String lastId = lastOpt.get().getActivityId();
                if (lastId != null && lastId.length() >= 7) {
                    return Integer.parseInt(lastId.substring(4, 7)) + 1;
                }
            }
        } catch (Exception ignored) {
        }
        return 1;
    }

    private String buildActivityId(int sequence, String codePart, Integer sessionId) {
        String seqPart = String.format("%03d", sequence);
        String sid = sessionId == null ? "0" : String.valueOf(sessionId);
        return String.format("ACT-%s-%s-%s", seqPart, codePart, sid);
    }

    private LocalDateTime buildActivityTime(LocalDateTime startTime, Integer timeInterval) {
        int interval = timeInterval == null ? 0 : timeInterval;
        return normalizeToSeconds(startTime).plusMinutes(interval);
    }

    private ActivityGenerationState buildActivityGenerationState(List<SamplingSchedule> samplingSchedules) {
        ActivityGenerationState generationState = new ActivityGenerationState();

        // Cari tahu menit relatif di mana insulin disuntikkan secara statis
        Integer injectMinute = samplingSchedules.stream()
                .filter(s -> Boolean.TRUE.equals(s.getInsulinInject()))
                .map(SamplingSchedule::getRelativeMinute)
                .findFirst()
                .orElse(null);

        // Cari tahu menit awal fase baseline (pre-insulin) untuk kalkulasi offset T-x
        Integer minBaseline = samplingSchedules.stream()
                .filter(s -> "pre-insulin".equalsIgnoreCase(s.getPhaseType()))
                .map(SamplingSchedule::getRelativeMinute)
                .min(Integer::compareTo)
                .orElse(0);

        generationState.injectionMinute = injectMinute;
        generationState.minBaselineOffset = minBaseline;

        try {
            var lastOpt = activityRepository.findTopByDeletedAtIsNullOrderByActivityIdDesc();
            if (lastOpt.isPresent()) {
                String lastId = lastOpt.get().getActivityId();
                if (lastId != null && lastId.length() >= 7) {
                    generationState.activitySequence = Integer.parseInt(lastId.substring(4, 7));
                }
            }
        } catch (Exception ignored) {
        }

        return generationState;
    }


    private List<SamplingSchedule> loadSamplingSchedules(Long protocolId) {
        return samplingScheduleRepository
                .findByProtocolIdAndDeletedAtIsNull(protocolId)
                .stream()
                .sorted(Comparator.comparing(SamplingSchedule::getSamplingScheduleId))
                .collect(Collectors.toList());
    }

    private List<Activity> buildActivitiesForDetail(Session session, User actor, Integer actorId, SamplingSchedule detail, ActivityGenerationState generationState) {
        List<Activity> activities = new ArrayList<>();
        String phaseType = detail.getPhaseType() == null ? "" : detail.getPhaseType().trim().toLowerCase();
        String phaseName = detail.getPhaseName();
        String phaseCode = detail.getPhaseCode();

        // Nilai blueprint menit relatif dibaca dari database SamplingSchedule
        int relativeMinute = detail.getRelativeMinute() == null ? 0 : detail.getRelativeMinute();
        
        LocalDateTime activityClockTime = session.getStartTime().plusMinutes(relativeMinute);

        if ("preparation".equals(phaseType)) {
            activities.add(buildActivity(session, actor, actorId, activityClockTime, 
                    phaseCode, "Pemeriksaan fisik awal (Vital Signs, Anthropometry, & Anamneses) pada fase " + phaseName, 
                    generationState, "PREPARATION_CHECK"));
        } 
        else if ("finalization".equals(phaseType)) {
            activities.add(buildActivity(session, actor, actorId, activityClockTime, 
                    phaseCode, "Evaluasi penghentian infus Dextrose 10% (GIR kembali mendekati 2 mg/kgBB/menit)", 
                    generationState, "DEXTROSE_STOP_CHECK"));
            activities.add(buildActivity(session, actor, actorId, activityClockTime, 
                    phaseCode, "Partisipan makan, diobservasi, pemeriksaan klinis dan antropometri akhir pada fase " + phaseName, 
                    generationState, "FINAL_OBSERVATION"));
        } 

        else {
            Integer minutesBeforeInjection = null;
            if (generationState.injectionMinute != null) {
                minutesBeforeInjection = relativeMinute - generationState.injectionMinute;
            }

            if (Boolean.TRUE.equals(detail.getInsulinInject())) {
                int baselineNumber = generationState.nextBaselineNumber();
                String basalCode = buildBaselineCode(minutesBeforeInjection);
                activities.add(buildActivity(session, actor, actorId, activityClockTime, basalCode, buildBaselineSampleDescription(baselineNumber, minutesBeforeInjection), generationState, "BLOOD_DRAW"));
                activities.add(buildActivity(session, actor, actorId, activityClockTime, "T0", buildInsulinInjectDescription(), generationState, "INSULIN_INJECTION"));
            } else if (Boolean.TRUE.equals(detail.getBloodRaw())) {
                if ("pre-insulin".equals(phaseType)) {
                    int baselineNumber = generationState.nextBaselineNumber();
                    String basalCode = buildBaselineCode(minutesBeforeInjection);
                    activities.add(buildActivity(session, actor, actorId, activityClockTime, basalCode, buildBaselineSampleDescription(baselineNumber, minutesBeforeInjection), generationState, "BLOOD_DRAW"));
                } else {
                    int glucoseNumber = generationState.nextGlucoseNumber();
                    String gdCode = "GD" + glucoseNumber;
                    activities.add(buildActivity(session, actor, actorId, activityClockTime, gdCode, buildGlucoseSampleDescription(glucoseNumber), generationState, "BLOOD_DRAW"));
                }
            }

            if (Boolean.TRUE.equals(detail.getPkSampleCollection())) {
                int pkcNumber = generationState.nextInsulinCheckNumber();
                String pkcCode = "PKC-" + pkcNumber;
                activities.add(buildActivity(session, actor, actorId, activityClockTime, pkcCode, buildInsulinCheckDescription(pkcNumber), generationState, "INSULIN_CHECK"));
            }
        }

        return activities;
    }

    private Activity buildActivity(
            Session session, 
            User actor, 
            Integer actorId, 
            LocalDateTime clockTime, 
            String codePart, 
            String activityDesc, 
            ActivityGenerationState generationState, 
            String type) {
            
        Activity activity = new Activity();
        int seq = generationState.nextActivitySequence();
        activity.setActivityId(buildActivityId(seq, codePart, session.getSessionId()));
        activity.setSession(session);
        activity.setActor(actor);
        
        activity.setTime(clockTime);
        
        activity.setActivityType(type);
        activity.setActivityDesc(activityDesc);
        activity.setActivityStatus(ActivityStatus.INQUEUE);
        activity.setStatus(EntityStatus.ACTIVE);
        activity.setCreatedBy(actorId);
        activity.setUpdatedBy(actorId);
        return activity;
    }

    private String buildBaselineSampleDescription(int baselineNumber, Integer minutesBeforeInsulin) {
        if (minutesBeforeInsulin == null) {
            minutesBeforeInsulin = 0;
        }
        if (minutesBeforeInsulin < 0) {
            int abs = Math.abs(minutesBeforeInsulin);
            return "Pengambilan darah untuk kadar glukosa darah basal ke-" + baselineNumber
                    + " " + abs + " menit sebelum penyuntikan insulin - T-" + abs;
        } else if (minutesBeforeInsulin == 0) {
            return "Pengambilan darah untuk kadar glukosa darah basal ke-" + baselineNumber + " pada waktu penyuntikan insulin - T0";
        } else {
            return "Pengambilan darah untuk kadar glukosa darah basal ke-" + baselineNumber
                    + " " + minutesBeforeInsulin + " menit setelah penyuntikan insulin - T+" + minutesBeforeInsulin;
        }
    }

    private String buildBaselineCode(Integer scheduledMinute) {
        if (scheduledMinute == null) return "T0";
        if (scheduledMinute < 0) {
            return "T-" + Math.abs(scheduledMinute);
        } else if (scheduledMinute == 0) {
            return "T0";
        } else {
            return "T+" + scheduledMinute;
        }
    }

    private String buildInsulinInjectDescription() {
        return "Injeksi insulin 0,5 U/kgBB SC";
    }

    private String buildGlucoseSampleDescription(int glucoseNumber) {
        return "Pengambilan sampel darah untuk kadar glukosa darah - GD" + glucoseNumber;
    }

    private String buildInsulinCheckDescription(int insulinCheckNumber) {
        return "Pengambilan darah untuk pemeriksaan kadar insulin (PK) & C-Peptide - PK-C" + insulinCheckNumber;
    }

    private String buildMonitoringDescription(SamplingSchedule detail) {
        return detail.getPhaseCode() == null ? "Monitoring jadwal aktivitas" : detail.getPhaseCode() + " - Monitoring jadwal aktivitas";
    }

    private static class ActivityGenerationState {
        private int elapsedMinutes = 0;
        private int baselineNumber = 0;
        private int glucoseNumber = 0;
        private int insulinCheckNumber = 0;
        private Map<String, Integer> detailOffsetMap = new HashMap<>();
        private Integer injectionOffsetMinutes = null;
        private Integer minBaselineOffset = null;
        private Integer injectionMinute = null;
        private int activitySequence = 0;

        private int nextBaselineNumber() {
            baselineNumber += 1;
            return baselineNumber;
        }

        private int nextGlucoseNumber() {
            glucoseNumber += 1;
            return glucoseNumber;
        }

        private int nextInsulinCheckNumber() {
            insulinCheckNumber += 1;
            return insulinCheckNumber;
        }

        private int nextActivitySequence() {
            activitySequence += 1;
            return activitySequence;
        }

        private void setActivitySequence(int activitySequence) {
            this.activitySequence = activitySequence;
        }
    }
}