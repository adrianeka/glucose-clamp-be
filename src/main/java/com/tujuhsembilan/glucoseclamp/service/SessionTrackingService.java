package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionActivityItemResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionTimelineResponse;
import com.tujuhsembilan.glucoseclamp.model.Activity;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import com.tujuhsembilan.glucoseclamp.repository.ActivityRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionTrackingService {

    private final SessionRepository sessionRepository;
    private final ActivityRepository activityRepository;

        @Transactional(readOnly = true)
    public ApiDataResponseBuilder getTimeline(Integer sessionId) {
        Optional<Session> sessionOptional = sessionRepository.findByIdAndDeletedAtIsNull(sessionId);
        if (sessionOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data session tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Session session = sessionOptional.get();
        List<Activity> activities = activityRepository.findBySessionIdAndDeletedAtIsNull(sessionId)
                .stream()
                .sorted(Comparator.comparing(Activity::getTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Activity::getActivityId))
                .collect(Collectors.toList());

        List<SessionActivityItemResponse> activityResponses = activities.stream()
                .map(this::toActivityItemResponse)
                .collect(Collectors.toList());

        int completedActivities = (int) activities.stream()
                .filter(this::isCompletedActivity)
                .count();

        int totalActivities = activities.size();
        int progressPercentage = totalActivities == 0 ? 0 : (int) Math.round((completedActivities * 100.0) / totalActivities);

        List<Activity> nextPendingActivities = findNextPendingActivities(activities);
        List<SessionActivityItemResponse> nextActivityResponses = nextPendingActivities.stream()
                .map(this::toActivityItemResponse)
                .collect(Collectors.toList());
        SessionActivityItemResponse nextActivity = nextActivityResponses.stream().findFirst().orElse(null);

        SessionStatus sessionStatus = session.getSessionStatus();
                if (sessionStatus == SessionStatus.IN_QUEUE && completedActivities > 0) {
            sessionStatus = SessionStatus.IN_PROGRESS;
        }
        if (totalActivities > 0 && completedActivities == totalActivities) {
            sessionStatus = SessionStatus.COMPLETED;
        }

        SessionTimelineResponse response = new SessionTimelineResponse();
        response.setSessionId(session.getSessionId());
        response.setParticipantId(session.getParticipant().getParticipantId());
        response.setParticipantName(session.getParticipant().getName());
        response.setProtocolId(session.getProtocol().getProtocolId());
        response.setProtocolName(session.getProtocol().getProtocolName());
        response.setVisitDate(session.getVisitDate());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setSessionStatus(sessionStatus);
        response.setTotalActivities(totalActivities);
        response.setCompletedActivities(completedActivities);
        response.setProgressPercentage(progressPercentage);
        response.setNextActivities(nextActivityResponses);
        response.setActivities(activityResponses);

        return ApiDataResponseBuilder.builder()
                .data(response)
                .message("Berhasil mendapatkan timeline session")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private SessionActivityItemResponse toActivityItemResponse(Activity activity) {
        return new SessionActivityItemResponse(
                activity.getActivityId(),
                activity.getTime(),
                activity.getActivityType(),
                activity.getActivityDesc(),
                activity.getActivityStatus());
    }

    private boolean isCompletedActivity(Activity activity) {
                return activity != null && activity.getActivityStatus() == ActivityStatus.COMPLETED;
    }

        private List<Activity> findNextPendingActivities(List<Activity> activities) {
                Optional<Activity> firstPending = activities.stream()
                                .filter(activity -> !isCompletedActivity(activity))
                                .findFirst();

                if (firstPending.isEmpty()) {
                        return List.of();
                }

                var nextTime = firstPending.get().getTime();
                return activities.stream()
                                .filter(activity -> !isCompletedActivity(activity))
                                .filter(activity -> Objects.equals(activity.getTime(), nextTime))
                                .collect(Collectors.toList());
        }
}