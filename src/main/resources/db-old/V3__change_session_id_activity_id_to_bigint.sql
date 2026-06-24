-- Drop foreign key constraints
ALTER TABLE session_devices
    DROP CONSTRAINT fks36mkeqd16d4kfhdbnwy7jbpp;

ALTER TABLE vital_signs
    DROP CONSTRAINT fkaysk4heq2xp7g8kvcw1lmi003;

ALTER TABLE activities
    DROP CONSTRAINT fkqgujgmbyam20pi1h6rhma3x1b;

ALTER TABLE anamneses
    DROP CONSTRAINT fkm7o489ssumk89kkcf2b19bd35;

ALTER TABLE anthropometries
    DROP CONSTRAINT fk9kascoelcssyp43qica6fwmpi;

ALTER TABLE infusion_monitorings
    DROP CONSTRAINT fk8lmoja8w26smxmeqt2yp1iv9h;

-- FK blood_samples -> activities
ALTER TABLE blood_samples
    DROP CONSTRAINT fkawws4yybgt9ejbkq65xj82dyu;

----------------------------------------------------
-- SESSION ID : INTEGER -> BIGINT
----------------------------------------------------

ALTER TABLE sessions
    ALTER COLUMN session_id TYPE BIGINT;

ALTER TABLE session_devices
    ALTER COLUMN session_id TYPE BIGINT;

ALTER TABLE vital_signs
    ALTER COLUMN session_id TYPE BIGINT;

ALTER TABLE activities
    ALTER COLUMN session_id TYPE BIGINT;

ALTER TABLE anamneses
    ALTER COLUMN session_id TYPE BIGINT;

ALTER TABLE anthropometries
    ALTER COLUMN session_id TYPE BIGINT;

ALTER TABLE infusion_monitorings
    ALTER COLUMN session_id TYPE BIGINT;

----------------------------------------------------
-- ACTIVITY ID : VARCHAR -> BIGINT
----------------------------------------------------

CREATE SEQUENCE IF NOT EXISTS activity_id_seq
    START WITH 1
    INCREMENT BY 1;

ALTER TABLE activities
    ALTER COLUMN activity_id TYPE BIGINT
    USING NULLIF(activity_id, '')::BIGINT;

ALTER TABLE activities
    ALTER COLUMN activity_id
    SET DEFAULT nextval('activity_id_seq');

ALTER TABLE blood_samples
    ALTER COLUMN activity_id TYPE BIGINT
    USING NULLIF(activity_id, '')::BIGINT;

----------------------------------------------------
-- RECREATE FK SESSIONS
----------------------------------------------------

ALTER TABLE session_devices
    ADD CONSTRAINT fks36mkeqd16d4kfhdbnwy7jbpp
    FOREIGN KEY (session_id)
    REFERENCES sessions(session_id);

ALTER TABLE vital_signs
    ADD CONSTRAINT fkaysk4heq2xp7g8kvcw1lmi003
    FOREIGN KEY (session_id)
    REFERENCES sessions(session_id);

ALTER TABLE activities
    ADD CONSTRAINT fkqgujgmbyam20pi1h6rhma3x1b
    FOREIGN KEY (session_id)
    REFERENCES sessions(session_id);

ALTER TABLE anamneses
    ADD CONSTRAINT fkm7o489ssumk89kkcf2b19bd35
    FOREIGN KEY (session_id)
    REFERENCES sessions(session_id);

ALTER TABLE anthropometries
    ADD CONSTRAINT fk9kascoelcssyp43qica6fwmpi
    FOREIGN KEY (session_id)
    REFERENCES sessions(session_id);

ALTER TABLE infusion_monitorings
    ADD CONSTRAINT fk8lmoja8w26smxmeqt2yp1iv9h
    FOREIGN KEY (session_id)
    REFERENCES sessions(session_id);

----------------------------------------------------
-- RECREATE FK ACTIVITIES
----------------------------------------------------

ALTER TABLE blood_samples
    ADD CONSTRAINT fkawws4yybgt9ejbkq65xj82dyu
    FOREIGN KEY (activity_id)
    REFERENCES activities(activity_id);