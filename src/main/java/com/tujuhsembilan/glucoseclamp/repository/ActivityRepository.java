package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL")
    List<Activity> findAllActive();
    @Query("SELECT a FROM Activity a WHERE a.activityId = ?1 AND a.deletedAt IS NULL")
    Optional<Activity> findByIdAndDeletedAtIsNull(String activityId);
    @Query("SELECT a FROM Activity a WHERE a.session.sessionId = ?1 AND a.deletedAt IS NULL")
    List<Activity> findBySessionIdAndDeletedAtIsNull(Integer sessionId);
    @Query("SELECT a FROM Activity a WHERE a.actor.userId = ?1 AND a.deletedAt IS NULL")
    List<Activity> findByActorIdAndDeletedAtIsNull(Integer actorId);

    Optional<Activity> findTopByDeletedAtIsNullOrderByActivityIdDesc();
}
