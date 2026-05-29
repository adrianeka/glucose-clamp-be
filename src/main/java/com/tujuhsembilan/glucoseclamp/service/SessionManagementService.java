package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.SessionCreateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.SessionUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionCreateResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionSummaryResponse;
import com.tujuhsembilan.glucoseclamp.model.Activity;
import com.tujuhsembilan.glucoseclamp.model.Patient;
import com.tujuhsembilan.glucoseclamp.model.Protocol;
import com.tujuhsembilan.glucoseclamp.model.ProtocolDetail;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import com.tujuhsembilan.glucoseclamp.repository.ActivityRepository;
import com.tujuhsembilan.glucoseclamp.repository.PatientRepository;
import com.tujuhsembilan.glucoseclamp.repository.ProtocolDetailRepository;
import com.tujuhsembilan.glucoseclamp.repository.ProtocolRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import com.tujuhsembilan.glucoseclamp.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionManagementService {

    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final ProtocolRepository protocolRepository;
    private final ProtocolDetailRepository protocolDetailRepository;
    private final ActivityRepository activityRepository;
    private final CurrentUserService currentUserService;

    public ApiDataResponseBuilder getAllSessions(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<SessionSummaryResponse> result = sessionRepository.findAllSessionSummaries(pageable);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data sesi")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }
    
    @Transactional
    public ApiDataResponseBuilder create(SessionCreateRequest request) {
        Integer actorId = currentUserService.getCurrentUserId();
        User actor = currentUserService.getCurrentUserEntity();
        Optional<Patient> patientOptional = patientRepository.findByIdAndDeletedAtIsNull(request.getPatientId());
        if (patientOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Patient tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<Protocol> protocolOptional = protocolRepository.findByIdAndDeletedAtIsNull(request.getProtocolId());
        if (protocolOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Session session = new Session();
        session.setPatient(patientOptional.get());
        session.setProtocol(protocolOptional.get());
        session.setVisitDate(request.getVisitDate());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setFastingHour(request.getFastingHour());
        session.setSessionStatus(SessionStatus.IN_QUEUE);
        session.setCreatedBy(actorId);
        session.setUpdatedBy(actorId);
        session.setStatus(session.getStatus());

        Session savedSession = sessionRepository.save(session);

        List<Activity> activities = generateActivitiesForSession(savedSession, actor, actorId);
        activityRepository.saveAll(activities);

        SessionCreateResponse response = buildSessionCreateResponse(savedSession, activities);

        return ApiDataResponseBuilder.builder()
                .data(response)
                .message("Session berhasil dibuat dan activity berhasil digenerate dari protocol detail")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

        @Transactional
        public ApiDataResponseBuilder update(Integer sessionId, SessionUpdateRequest request) {
        Integer actorId = currentUserService.getCurrentUserId();
        User actor = currentUserService.getCurrentUserEntity();

        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId)
            .orElse(null);
        if (session == null) {
            return ApiDataResponseBuilder.builder()
                .message("Session tidak ditemukan")
                .statusCode(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .build();
        }

        if (session.getSessionStatus() != SessionStatus.IN_QUEUE) {
            return ApiDataResponseBuilder.builder()
                .message("Session hanya bisa diedit saat status masih IN QUEUE")
                .statusCode(HttpStatus.CONFLICT.value())
                .status(HttpStatus.CONFLICT)
                .build();
        }

        Optional<Protocol> protocolOptional = protocolRepository.findByIdAndDeletedAtIsNull(request.getProtocolId());
        if (protocolOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                .message("Protocol tidak ditemukan")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }

        session.setProtocol(protocolOptional.get());
        session.setVisitDate(request.getVisitDate());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setFastingHour(request.getFastingHour());
        session.setUpdatedBy(actorId);
        sessionRepository.save(session);

        softDeleteActivitiesForSession(sessionId, actorId);

        List<Activity> activities = generateActivitiesForSession(session, actor, actorId);
        activityRepository.saveAll(activities);

        SessionCreateResponse response = buildSessionCreateResponse(session, activities);

        return ApiDataResponseBuilder.builder()
            .data(response)
            .message("Session berhasil diperbarui dan activity berhasil diregenerate")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
        }

    private LocalDateTime buildActivityTime(LocalDateTime startTime, Integer timeInterval) {
        int interval = timeInterval == null ? 0 : timeInterval;
        return startTime.plusMinutes(interval);
    }

    private List<Activity> generateActivitiesForSession(Session session, User actor, Integer actorId) {
        List<ProtocolDetail> protocolDetails = loadProtocolDetails(session.getProtocol().getProtocolId());
        ActivityGenerationState generationState = buildActivityGenerationState(protocolDetails);

        List<Activity> activities = new ArrayList<>();
        for (ProtocolDetail detail : protocolDetails) {
            activities.addAll(buildActivitiesForDetail(session, actor, actorId, detail, generationState));
        }
        return activities;
    }

    private List<ProtocolDetail> loadProtocolDetails(String protocolId) {
        return protocolDetailRepository
                .findByProtocolIdAndDeletedAtIsNull(protocolId)
                .stream()
                .sorted(Comparator.comparing(ProtocolDetail::getProtocolDetailId))
                .collect(Collectors.toList());
    }

    private void softDeleteActivitiesForSession(Integer sessionId, Integer actorId) {
        List<Activity> existingActivities = activityRepository.findBySessionIdAndDeletedAtIsNull(sessionId);
        for (Activity activity : existingActivities) {
            activity.setDeletedAt(java.time.LocalDateTime.now());
            activity.setDeletedBy(actorId);
            activity.setUpdatedBy(actorId);
        }
        activityRepository.saveAll(existingActivities);
    }

    private SessionCreateResponse buildSessionCreateResponse(Session session, List<Activity> activities) {
        List<String> activityIds = activities.stream()
                .map(Activity::getActivityId)
                .collect(Collectors.toList());

        return new SessionCreateResponse(
                session.getSessionId(),
                session.getPatient().getPatientId(),
                session.getProtocol().getProtocolId(),
                activityIds.size(),
                activityIds);
    }

    private ActivityGenerationState buildActivityGenerationState(List<ProtocolDetail> protocolDetails) {
        ActivityGenerationState generationState = new ActivityGenerationState();
        try {
            var lastOpt = activityRepository.findTopByDeletedAtIsNullOrderByActivityIdDesc();
            if (lastOpt.isPresent()) {
                String lastId = lastOpt.get().getActivityId();
                if (lastId != null && lastId.length() >= 7) {
                    try {
                        int lastSeq = Integer.parseInt(lastId.substring(4, 7));
                        generationState.setActivitySequence(lastSeq);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }

        int cum = 0;
        Integer minBaseline = null;
        for (ProtocolDetail d : protocolDetails) {
            generationState.detailOffsetMap.put(d.getProtocolDetailId(), cum);
            String phase = d.getPhaseCode() == null ? "" : d.getPhaseCode().trim();
            if ("baseline".equalsIgnoreCase(phase)) {
                if (minBaseline == null || cum < minBaseline) minBaseline = cum;
            }
            if (Boolean.TRUE.equals(d.getInsulinInject()) && generationState.injectionOffsetMinutes == null) {
                generationState.injectionOffsetMinutes = cum;
            }
            int interval = d.getTimeInterval() == null ? 0 : d.getTimeInterval();
            cum += interval;
        }
        generationState.minBaselineOffset = minBaseline == null ? 0 : minBaseline;
        return generationState;
    }

    private List<Activity> buildActivitiesForDetail(Session session, User actor, Integer actorId, ProtocolDetail detail, ActivityGenerationState generationState) {
        List<Activity> activities = new ArrayList<>();
        String phaseCode = detail.getPhaseCode() == null ? "" : detail.getPhaseCode().trim();
        boolean isBaselinePhase = "baseline".equalsIgnoreCase(phaseCode);
        // compute offsets
        Integer detailOffset = generationState.detailOffsetMap.get(detail.getProtocolDetailId());
        if (detailOffset == null) detailOffset = generationState.elapsedMinutes;
        Integer minutesBeforeInjection = null;
        if (generationState.injectionOffsetMinutes != null) {
            minutesBeforeInjection = detailOffset - generationState.injectionOffsetMinutes;
        }
        // schedule time so that earliest baseline maps to session.startTime
        int timeOffsetFromStart = detailOffset - generationState.minBaselineOffset;

        if (Boolean.TRUE.equals(detail.getInsulinInject())) {
            int baselineNumber = generationState.nextBaselineNumber();
            String basalCode = buildBaselineCode(minutesBeforeInjection);
            activities.add(buildActivity(session, actor, actorId, detail, timeOffsetFromStart, basalCode, buildBaselineSampleDescription(baselineNumber, minutesBeforeInjection), generationState, "BLOOD_RAW"));
            // insulin injection at T0
            activities.add(buildActivity(session, actor, actorId, detail, timeOffsetFromStart, "T0", buildInsulinInjectDescription(), generationState, "INSULIN_CHECK"));
        } else if (Boolean.TRUE.equals(detail.getBloodRaw())) {
            if (isBaselinePhase) {
                int baselineNumber = generationState.nextBaselineNumber();
                String basalCode = buildBaselineCode(minutesBeforeInjection);
                activities.add(buildActivity(session, actor, actorId, detail, timeOffsetFromStart, basalCode, buildBaselineSampleDescription(baselineNumber, minutesBeforeInjection), generationState, "BLOOD_RAW"));
            } else {
                int glucoseNumber = generationState.nextGlucoseNumber();
                String gdCode = "GD" + glucoseNumber;
                activities.add(buildActivity(session, actor, actorId, detail, timeOffsetFromStart, gdCode, buildGlucoseSampleDescription(glucoseNumber), generationState, "BLOOD_RAW"));
            }
        }

        if (Boolean.TRUE.equals(detail.getInsulinCheck())) {
            int pkcNumber = generationState.nextInsulinCheckNumber();
            String pkcCode = "PKC-" + pkcNumber;
            activities.add(buildActivity(session, actor, actorId, detail, timeOffsetFromStart, pkcCode, buildInsulinCheckDescription(pkcNumber), generationState, "INSULIN_CHECK"));
        }

        if (activities.isEmpty()) {
            activities.add(buildActivity(session, actor, actorId, detail, timeOffsetFromStart, "MON", buildMonitoringDescription(detail), generationState, "MONITORING"));
        }

        int interval = detail.getTimeInterval() == null ? 0 : detail.getTimeInterval();
        generationState.elapsedMinutes += interval;
        return activities;
    }

    private Activity buildActivity(Session session, User actor, Integer actorId, ProtocolDetail detail, Integer scheduledMinute, String codePart, String activityDesc, ActivityGenerationState generationState, String type) {
        Activity activity = new Activity();
        int seq = generationState.nextActivitySequence();
        activity.setActivityId(buildActivityId(seq, codePart, session.getSessionId()));
        activity.setSession(session);
        activity.setActor(actor);
        activity.setTime(buildActivityTime(session.getStartTime(), scheduledMinute));
        activity.setActivityType(type);
        activity.setActivityDesc(activityDesc);
        activity.setActivityStatus(ActivityStatus.SCHEDULED);
        activity.setCreatedBy(actorId);
        activity.setUpdatedBy(actorId);
        return activity;
    }

    private String buildActivityId(int sequence, String codePart, Integer sessionId) {
        String seqPart = String.format("%03d", sequence);
        String sid = sessionId == null ? "0" : String.valueOf(sessionId);
        return String.format("ACT-%s-%s-%s", seqPart, codePart, sid);
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

    private String buildMonitoringDescription(ProtocolDetail detail) {
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

        private void setActivitySequence(int seq) {
            this.activitySequence = seq;
        }
    }
}