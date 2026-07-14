-- public.activity_id_seq definition
-- DROP SEQUENCE public.activity_id_seq;
CREATE SEQUENCE public.activity_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.anamnesis_id_seq definition
-- DROP SEQUENCE public.anamnesis_id_seq;
CREATE SEQUENCE public.anamnesis_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.anthro_id_seq definition
-- DROP SEQUENCE public.anthro_id_seq;
CREATE SEQUENCE public.anthro_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.audit_id_seq definition
-- DROP SEQUENCE public.audit_id_seq;
CREATE SEQUENCE public.audit_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.blood_samples_blood_sample_id_seq definition
-- DROP SEQUENCE public.blood_samples_blood_sample_id_seq;
CREATE SEQUENCE public.blood_samples_blood_sample_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.device_id_seq definition
-- DROP SEQUENCE public.device_id_seq;
CREATE SEQUENCE public.device_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.global_config_id_seq definition
-- DROP SEQUENCE public.global_config_id_seq;
CREATE SEQUENCE public.global_config_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.infusion_monitorings_infusion_id_seq definition
-- DROP SEQUENCE public.infusion_monitorings_infusion_id_seq;
CREATE SEQUENCE public.infusion_monitorings_infusion_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.lab_results_lab_result_id_seq definition
-- DROP SEQUENCE public.lab_results_lab_result_id_seq;
CREATE SEQUENCE public.lab_results_lab_result_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.notifications_notification_id_seq definition
-- DROP SEQUENCE public.notifications_notification_id_seq;
CREATE SEQUENCE public.notifications_notification_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.phase_conf_id_seq definition
-- DROP SEQUENCE public.phase_conf_id_seq;
CREATE SEQUENCE public.phase_conf_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.protocols_protocol_id_seq definition
-- DROP SEQUENCE public.protocols_protocol_id_seq;
CREATE SEQUENCE public.protocols_protocol_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.role_access_id_seq definition
-- DROP SEQUENCE public.role_access_id_seq;
CREATE SEQUENCE public.role_access_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.role_id_seq definition
-- DROP SEQUENCE public.role_id_seq;
CREATE SEQUENCE public.role_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.sampling_schedules_sampling_schedule_id_seq definition
-- DROP SEQUENCE public.sampling_schedules_sampling_schedule_id_seq;
CREATE SEQUENCE public.sampling_schedules_sampling_schedule_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.session_device_id_seq definition
-- DROP SEQUENCE public.session_device_id_seq;
CREATE SEQUENCE public.session_device_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.session_id_seq definition
-- DROP SEQUENCE public.session_id_seq;
CREATE SEQUENCE public.session_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.user_id_seq definition
-- DROP SEQUENCE public.user_id_seq;
CREATE SEQUENCE public.user_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.users_user_id_seq definition
-- DROP SEQUENCE public.users_user_id_seq;
CREATE SEQUENCE public.users_user_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- public.vital_id_seq definition
-- DROP SEQUENCE public.vital_id_seq;
CREATE SEQUENCE public.vital_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- public.access_menus definition
-- DROP TABLE public.access_menus;
CREATE TABLE public.access_menus (
	created_by int4 NULL,
	deleted_by int4 NULL,
	menu_id int4 NOT NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	menu_name varchar(100) NOT NULL,
	CONSTRAINT access_menus_pkey PRIMARY KEY (menu_id),
	CONSTRAINT access_menus_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


-- public.devices definition
-- DROP TABLE public.devices;
CREATE TABLE public.devices (
	created_by int4 NULL,
	deleted_by int4 NULL,
	device_id int4 NOT NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	last_calibration_date timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	device_brand varchar(100) NULL,
	device_type varchar(100) NOT NULL,
	serial_number varchar(100) NULL,
	CONSTRAINT devices_pkey PRIMARY KEY (device_id),
	CONSTRAINT devices_serial_number_key UNIQUE (serial_number),
	CONSTRAINT devices_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


-- public.global_configurations definition
-- DROP TABLE public.global_configurations;
CREATE TABLE public.global_configurations (
	created_by int4 NULL,
	deleted_by int4 NULL,
	gconf_id numeric(38) NOT NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	gconf_code varchar(100) NOT NULL,
	gconf_desc varchar(500) NULL,
	gconf_title varchar(255) NULL,
	gconf_value varchar(255) NOT NULL,
	CONSTRAINT global_configurations_pkey PRIMARY KEY (gconf_id),
	CONSTRAINT global_configurations_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


-- public.notifications definition
-- DROP TABLE public.notifications;
CREATE TABLE public.notifications (
	notification_id bigserial NOT NULL,
	session_id varchar(50) NOT NULL,
	activity_id varchar(50) NOT NULL,
	title varchar(100) NOT NULL,
	description text NOT NULL,
	is_read bool DEFAULT false NOT NULL,
	status varchar(20) DEFAULT 'ACTIVE'::character varying NULL,
	created_at timestamp NULL,
	created_by int4 NULL,
	updated_at timestamp NULL,
	updated_by int4 NULL,
	deleted_at timestamp NULL,
	deleted_by int4 NULL,
	CONSTRAINT notifications_pkey PRIMARY KEY (notification_id)
);


-- public.participants definition
-- DROP TABLE public.participants;
CREATE TABLE public.participants (
	created_by int4 NULL,
	deleted_by int4 NULL,
	dob date NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	gender varchar(20) NULL,
	number_phone varchar(20) NULL,
	status varchar(20) NULL,
	participant_id varchar(50) NOT NULL,
	medical_record_no varchar(100) NOT NULL,
	"name" varchar(200) NOT NULL,
	CONSTRAINT participants_medical_record_no_key UNIQUE (medical_record_no),
	CONSTRAINT participants_pkey PRIMARY KEY (participant_id),
	CONSTRAINT participants_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


-- public.phase_configurations definition
-- DROP TABLE public.phase_configurations;
CREATE TABLE public.phase_configurations (
	created_by int4 NULL,
	deleted_by int4 NULL,
	phase_conf_priority int4 NOT NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	phase_conf_id int8 NOT NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	phase_conf_code varchar(100) NOT NULL,
	phase_conf_type varchar(100) NOT NULL,
	phase_conf_name varchar(255) NOT NULL,
	CONSTRAINT phase_configurations_phase_conf_code_key UNIQUE (phase_conf_code),
	CONSTRAINT phase_configurations_phase_conf_priority_key UNIQUE (phase_conf_priority),
	CONSTRAINT phase_configurations_pkey PRIMARY KEY (phase_conf_id),
	CONSTRAINT phase_configurations_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


-- public.protocols definition
-- DROP TABLE public.protocols;
CREATE TABLE public.protocols (
	created_by int4 NULL,
	deleted_by int4 NULL,
	duration_hours numeric(10, 2) NULL,
	glucose_drop_trigger_percentage numeric(5, 2) NULL,
	glucose_target_max numeric(10, 2) NULL,
	glucose_target_max_extreme numeric(10, 2) NULL,
	glucose_target_min numeric(10, 2) NULL,
	glucose_target_min_extreme numeric(10, 2) NULL,
	initial_glucose_infusion_rate numeric(10, 2) NULL,
	updated_by int4 NULL,
	"version" float4 NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	protocol_id bigserial NOT NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	glucose_target_unit varchar(50) NULL,
	initial_glucose_infusion_rate_unit varchar(50) NULL,
	insulin_dose_unit varchar(50) NULL,
	protocol_code varchar(50) NOT NULL,
	insulin_dose_rule varchar(500) NULL,
	protocol_name varchar(255) NOT NULL,
	CONSTRAINT protocols_pkey PRIMARY KEY (protocol_id),
	CONSTRAINT protocols_protocol_code_key UNIQUE (protocol_code),
	CONSTRAINT protocols_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


-- public.roles definition
-- DROP TABLE public.roles;
CREATE TABLE public.roles (
	created_by int4 NULL,
	deleted_by int4 NULL,
	role_id int4 NOT NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	role_name varchar(100) NOT NULL,
	CONSTRAINT roles_pkey PRIMARY KEY (role_id),
	CONSTRAINT roles_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[])))
);


-- public.role_access definition
-- DROP TABLE public.role_access;
CREATE TABLE public.role_access (
	can_add bool NULL,
	can_delete bool NULL,
	can_edit bool NULL,
	can_view bool NULL,
	created_by int4 NULL,
	deleted_by int4 NULL,
	menu_id int4 NOT NULL,
	role_access_id int4 NOT NULL,
	role_id int4 NOT NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	CONSTRAINT role_access_pkey PRIMARY KEY (role_access_id),
	CONSTRAINT role_access_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fk8h4ntdxojn8pr3g0gdv3p2plq FOREIGN KEY (menu_id) REFERENCES public.access_menus(menu_id),
	CONSTRAINT fkn9xcmxjxq6cv8hj88gyvmbmll FOREIGN KEY (role_id) REFERENCES public.roles(role_id)
);


-- public.sampling_schedules definition
-- DROP TABLE public.sampling_schedules;
CREATE TABLE public.sampling_schedules (
	blood_raw bool NULL,
	created_by int4 NULL,
	deleted_by int4 NULL,
	insulin_inject bool NULL,
	phase_duration int4 NULL,
	pk_sample_collection bool NULL,
	relative_minute int4 NULL,
	time_interval int4 NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	protocol_id int8 NOT NULL,
	sampling_schedule_id bigserial NOT NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	phase_code varchar(50) NULL,
	phase_type varchar(100) NULL,
	phase_name varchar(255) NULL,
	schedule_code varchar(255) NOT NULL,
	CONSTRAINT sampling_schedules_pkey PRIMARY KEY (sampling_schedule_id),
	CONSTRAINT sampling_schedules_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fkbkx6m6bln670dujmwmjupcimr FOREIGN KEY (protocol_id) REFERENCES public.protocols(protocol_id)
);


-- public.sessions definition
-- DROP TABLE public.sessions;
CREATE TABLE public.sessions (
	created_by int4 NULL,
	deleted_by int4 NULL,
	fasting_hour int4 NULL,
	updated_by int4 NULL,
	visit_date date NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	end_time timestamp(6) NULL,
	protocol_id int8 NOT NULL,
	session_id int8 NOT NULL,
	start_time timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	participant_id varchar(50) NOT NULL,
	session_status varchar(50) NULL,
	end_reason_category varchar(100) NULL,
	end_reason_detail varchar(1000) NULL,
	CONSTRAINT sessions_pkey PRIMARY KEY (session_id),
	CONSTRAINT sessions_session_status_check CHECK (((session_status)::text = ANY ((ARRAY['PREP'::character varying, 'RUNNING'::character varying, 'HOLD'::character varying, 'COMPLETED'::character varying])::text[]))),
	CONSTRAINT sessions_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fkhcfxvnj2acip37p2bgoqx95pp FOREIGN KEY (protocol_id) REFERENCES public.protocols(protocol_id),
	CONSTRAINT fkjyjxl9g5g40wkgapqlcth6o99 FOREIGN KEY (participant_id) REFERENCES public.participants(participant_id)
);


-- public.users definition
-- DROP TABLE public.users;
CREATE TABLE public.users (
	created_by int4 NULL,
	deleted_by int4 NULL,
	role_id int4 NOT NULL,
	updated_by int4 NULL,
	user_id int4 NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	position_name varchar(100) NOT NULL,
	username varchar(100) NOT NULL,
	email varchar(150) NOT NULL,
	"name" varchar(150) NOT NULL,
	"password" varchar(255) NOT NULL,
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (user_id),
	CONSTRAINT users_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT users_username_key UNIQUE (username),
	CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(role_id)
);


-- public.vital_signs definition
-- DROP TABLE public.vital_signs;
CREATE TABLE public.vital_signs (
	assigned_by int4 NULL,
	created_by int4 NULL,
	deleted_by int4 NULL,
	diastolic int4 NULL,
	pulse int4 NULL,
	respiratory_rate int4 NULL,
	spo2 numeric(10, 2) NULL,
	systolic int4 NULL,
	temperature_c numeric(10, 2) NULL,
	updated_by int4 NULL,
	vital_id int4 NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	measured_at timestamp(6) NULL,
	session_id int8 NOT NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	CONSTRAINT vital_signs_pkey PRIMARY KEY (vital_id),
	CONSTRAINT vital_signs_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fkaysk4heq2xp7g8kvcw1lmi003 FOREIGN KEY (session_id) REFERENCES public.sessions(session_id),
	CONSTRAINT fkeimdnksesyhec7q437jrgo9ij FOREIGN KEY (assigned_by) REFERENCES public.users(user_id)
);


-- public.activities definition
-- DROP TABLE public.activities;
CREATE TABLE public.activities (
	actor_id int4 NOT NULL,
	created_by int4 NULL,
	deleted_by int4 NULL,
	"minute" int4 NULL,
	updated_by int4 NULL,
	activity_id int8 NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	session_id int8 NOT NULL,
	"time" timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	activity_status varchar(50) NULL,
	activity_type varchar(100) NULL,
	activity_desc varchar(500) NULL,
	phase_code varchar(255) NULL,
	phase_name varchar(255) NULL,
	phase_type varchar(255) NULL,
	schedule_code varchar(255) NULL,
	CONSTRAINT activities_activity_status_check CHECK (((activity_status)::text = ANY ((ARRAY['INQUEUE'::character varying, 'NEXT_ACTIVITY'::character varying, 'IN_PROGRESS'::character varying, 'COMPLETED'::character varying])::text[]))),
	CONSTRAINT activities_pkey PRIMARY KEY (activity_id),
	CONSTRAINT activities_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fkmfjrc8jdvy0yrr7x67qmrxkue FOREIGN KEY (actor_id) REFERENCES public.users(user_id),
	CONSTRAINT fkqgujgmbyam20pi1h6rhma3x1b FOREIGN KEY (session_id) REFERENCES public.sessions(session_id)
);


-- public.anamneses definition
-- DROP TABLE public.anamneses;
CREATE TABLE public.anamneses (
	anamnesis_id int4 NOT NULL,
	assigned_by int4 NULL,
	created_by int4 NULL,
	"date" date NULL,
	deleted_by int4 NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	session_id int8 NOT NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	chief_complaint varchar(500) NULL,
	medical_history varchar(1000) NULL,
	CONSTRAINT anamneses_pkey PRIMARY KEY (anamnesis_id),
	CONSTRAINT anamneses_session_id_key UNIQUE (session_id),
	CONSTRAINT anamneses_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fkm7o489ssumk89kkcf2b19bd35 FOREIGN KEY (session_id) REFERENCES public.sessions(session_id),
	CONSTRAINT fksju63sr5ps09atugxkwj88ri6 FOREIGN KEY (assigned_by) REFERENCES public.users(user_id)
);


-- public.anthropometries definition
-- DROP TABLE public.anthropometries;
CREATE TABLE public.anthropometries (
	anthro_id int4 NOT NULL,
	assigned_by int4 NULL,
	bmi numeric(10, 2) NULL,
	created_by int4 NULL,
	deleted_by int4 NULL,
	height_cm numeric(10, 2) NULL,
	updated_by int4 NULL,
	waist_circumference_cm numeric(10, 2) NULL,
	weight_kg numeric(10, 2) NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	measured_at timestamp(6) NULL,
	session_id int8 NOT NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	CONSTRAINT anthropometries_pkey PRIMARY KEY (anthro_id),
	CONSTRAINT anthropometries_session_id_key UNIQUE (session_id),
	CONSTRAINT anthropometries_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fk9kascoelcssyp43qica6fwmpi FOREIGN KEY (session_id) REFERENCES public.sessions(session_id),
	CONSTRAINT fksry1pmw02ewmxlipeuihn9hx4 FOREIGN KEY (assigned_by) REFERENCES public.users(user_id)
);


-- public.audit_trails definition
-- DROP TABLE public.audit_trails;
CREATE TABLE public.audit_trails (
	created_by int4 NOT NULL,
	audit_id int8 NOT NULL,
	created_at timestamp(6) NOT NULL,
	"action" varchar(20) NOT NULL,
	ip_address varchar(50) NULL,
	record_id varchar(100) NOT NULL,
	table_name varchar(100) NOT NULL,
	new_value text NULL,
	old_value text NULL,
	user_agent text NULL,
	CONSTRAINT audit_trails_pkey PRIMARY KEY (audit_id),
	CONSTRAINT fkbp365fu1myhlkrl738v4yewsa FOREIGN KEY (created_by) REFERENCES public.users(user_id)
);


-- public.blood_samples definition
-- DROP TABLE public.blood_samples;
CREATE TABLE public.blood_samples (
	collected_by int4 NULL,
	created_by int4 NULL,
	deleted_by int4 NULL,
	updated_by int4 NULL,
	volume_ml int4 NULL,
	activity_id int8 NULL,
	blood_sample_id bigserial NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	sample_time timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	sample_code varchar(100) NULL,
	sample_type varchar(100) NULL,
	tube_type varchar(100) NULL,
	CONSTRAINT blood_samples_pkey PRIMARY KEY (blood_sample_id),
	CONSTRAINT blood_samples_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fkawws4yybgt9ejbkq65xj82dyu FOREIGN KEY (activity_id) REFERENCES public.activities(activity_id)
);


-- public.infusion_monitorings definition
-- DROP TABLE public.infusion_monitorings;
CREATE TABLE public.infusion_monitorings (
	actual_gir numeric(10, 2) NULL,
	created_by int4 NULL,
	deleted_by int4 NULL,
	flow_rate_ml_hr numeric(10, 2) NULL,
	glucose_value numeric(10, 2) NULL,
	monitored_by int4 NULL,
	recommended_gir numeric(10, 2) NULL,
	updated_by int4 NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	infusion_id bigserial NOT NULL,
	session_id int8 NOT NULL,
	"time" timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	adjustment_note varchar(500) NULL,
	CONSTRAINT infusion_monitorings_pkey PRIMARY KEY (infusion_id),
	CONSTRAINT infusion_monitorings_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fk8lmoja8w26smxmeqt2yp1iv9h FOREIGN KEY (session_id) REFERENCES public.sessions(session_id)
);


-- public.lab_results definition
-- DROP TABLE public.lab_results;
CREATE TABLE public.lab_results (
	created_by int4 NULL,
	deleted_by int4 NULL,
	reference_range_max numeric(10, 2) NULL,
	reference_range_min numeric(10, 2) NULL,
	updated_by int4 NULL,
	value numeric(10, 2) NULL,
	verified_by int4 NULL,
	blood_sample_id int8 NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	lab_result_id bigserial NOT NULL,
	updated_at timestamp(6) NULL,
	abnormal_flag varchar(20) NULL,
	status varchar(20) NULL,
	unit varchar(50) NULL,
	parameter_name varchar(100) NOT NULL,
	CONSTRAINT lab_results_pkey PRIMARY KEY (lab_result_id),
	CONSTRAINT lab_results_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fkccx0h3no35rryj7upsoayd7y9 FOREIGN KEY (verified_by) REFERENCES public.users(user_id),
	CONSTRAINT fkd2c4d2aa1gc6odytetkl6b52j FOREIGN KEY (blood_sample_id) REFERENCES public.blood_samples(blood_sample_id)
);


-- public.session_devices definition
-- DROP TABLE public.session_devices;
CREATE TABLE public.session_devices (
	assigned_by int4 NULL,
	created_by int4 NULL,
	deleted_by int4 NULL,
	device_id int4 NOT NULL,
	session_device_id int4 NOT NULL,
	updated_by int4 NULL,
	assigned_at timestamp(6) NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	session_id int8 NOT NULL,
	updated_at timestamp(6) NULL,
	status varchar(20) NULL,
	CONSTRAINT session_devices_pkey PRIMARY KEY (session_device_id),
	CONSTRAINT session_devices_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'DELETED'::character varying])::text[]))),
	CONSTRAINT fk364gythe8phcbrnxbbp8xrjgb FOREIGN KEY (assigned_by) REFERENCES public.users(user_id),
	CONSTRAINT fkj3d119b0yjip9yu6wxg5rimf FOREIGN KEY (device_id) REFERENCES public.devices(device_id),
	CONSTRAINT fks36mkeqd16d4kfhdbnwy7jbpp FOREIGN KEY (session_id) REFERENCES public.sessions(session_id)
);

-- =========================================================================
-- 1. SEED DATA MASTER ROLES
-- =========================================================================
INSERT INTO public.roles (role_id, role_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES 
(1, 'Superadmin', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(2, 'Admin', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(3, 'Supervisor', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(4, 'Operator Insulin', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(5, 'Operator Analyzer', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(6, 'Operator Pump', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(7, 'Operator Pump 2', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 6, '2026-05-21 07:10:00', 6, 'DELETED')
ON CONFLICT (role_id) DO UPDATE SET 
    role_name = EXCLUDED.role_name, 
    created_at = EXCLUDED.created_at, 
    created_by = EXCLUDED.created_by, 
    updated_at = EXCLUDED.updated_at, 
    updated_by = EXCLUDED.updated_by, 
    deleted_at = EXCLUDED.deleted_at, 
    deleted_by = EXCLUDED.deleted_by, 
    status = EXCLUDED.status;

-- =========================================================================
-- 2. SEED DATA MASTER ACCESS MENUS
-- =========================================================================
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

-- =========================================================================
-- 3. SEED DATA MASTER USERS
-- Password di bawah ini adalah hasil enkripsi BCrypt terverifikasi dari: "hash123"
-- =========================================================================
INSERT INTO public.users (user_id, role_id, position_name, name, username, email, password, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES 
(1, 1, 'SUPER ADMIN', 'Super Admin', 'superadmin', 'superadmin@mail.com', '$2a$10$EzxHPlkpyymlpudwzZ5i/eMISRhdPTdpwqRuP2lbfuHuh1.bqYH3K', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(2, 2, 'ADMINISTRASI', 'Admin RSCM', 'admin', 'admin@mail.com', '$2a$10$EzxHPlkpyymlpudwzZ5i/eMISRhdPTdpwqRuP2lbfuHuh1.bqYH3K', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(3, 3, 'DOKTER PENELITI', 'Dr Budi', 'budi', 'budi@mail.com', '$2a$10$EzxHPlkpyymlpudwzZ5i/eMISRhdPTdpwqRuP2lbfuHuh1.bqYH3K', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(4, 4, 'PERAWAT/DOKTER', 'Fitri', 'fitri', 'fitri@mail.com', '$2a$10$EzxHPlkpyymlpudwzZ5i/eMISRhdPTdpwqRuP2lbfuHuh1.bqYH3K', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(5, 5, 'ANALIS', 'Rina', 'rina', 'rina@mail.com', '$2a$10$EzxHPlkpyymlpudwzZ5i/eMISRhdPTdpwqRuP2lbfuHuh1.bqYH3K', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(6, 6, 'PERAWAT', 'Agus', 'agus', 'agus@mail.com', '$2a$10$EzxHPlkpyymlpudwzZ5i/eMISRhdPTdpwqRuP2lbfuHuh1.bqYH3K', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(7, 6, 'PERAWAT', 'Bagas', 'bagas', 'bagas@mail.com', '$2a$10$EzxHPlkpyymlpudwzZ5i/eMISRhdPTdpwqRuP2lbfuHuh1.bqYH3K', '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'INACTIVE')
ON CONFLICT (user_id) DO UPDATE SET 
    role_id = EXCLUDED.role_id, 
    position_name = EXCLUDED.position_name, 
    name = EXCLUDED.name, 
    username = EXCLUDED.username, 
    email = EXCLUDED.email, 
    password = EXCLUDED.password, 
    created_at = EXCLUDED.created_at, 
    created_by = EXCLUDED.created_by, 
    updated_at = EXCLUDED.updated_at, 
    updated_by = EXCLUDED.updated_by, 
    deleted_at = EXCLUDED.deleted_at, 
    deleted_by = EXCLUDED.deleted_by, 
    status = EXCLUDED.status;

-- =========================================================================
-- 4. SEED DATA MASTER ROLE ACCESS
-- =========================================================================
INSERT INTO public.role_access (role_access_id, role_id, menu_id, can_view, can_add, can_edit, can_delete, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES 
-- SUPERADMIN / SUPERUSER (ROLE ID = 1)
(1,  1, 1,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(2,  1, 2,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(3,  1, 3,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(4,  1, 4,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(5,  1, 5,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(6,  1, 6,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(7,  1, 7,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(8,  1, 8,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(9,  1, 9,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(10, 1, 10, true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),

-- ADMIN (ROLE ID = 2)
(11, 2, 1,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(12, 2, 2,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(13, 2, 3,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(14, 2, 4,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(15, 2, 5,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(16, 2, 6,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(17, 2, 7,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(18, 2, 8,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(19, 2, 9,  true, true, true, true, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),

-- SUPERVISOR (ROLE ID = 3)
(20, 3, 1,  false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(21, 3, 2,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(22, 3, 3,  true,  true,  true,  true,  '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(23, 3, 4,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(24, 3, 5,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(25, 3, 6,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(26, 3, 7,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(27, 3, 8,  false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(28, 3, 9,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(29, 3, 10, false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),

-- ANALYZER OPERATOR (ROLE ID = 5)
(30, 5, 1,  false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(31, 5, 2,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(32, 5, 3,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(33, 5, 4,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(34, 5, 5,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(35, 5, 6,  true,  true,  true,  true,  '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(36, 5, 7,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(37, 5, 8,  false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(38, 5, 9,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(39, 5, 10, false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),

-- PUMP OPERATOR (ROLE ID = 6)
(40, 6, 1,  false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(41, 6, 2,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(42, 6, 3,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(43, 6, 4,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(44, 6, 5,  true,  true,  true,  true,  '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(45, 6, 6,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(46, 6, 7,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(47, 6, 8,  false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(48, 6, 9,  true,  false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE'),
(49, 6, 10, false, false, false, false, '2026-05-21 07:10:00', 1, '2026-05-21 07:10:00', 1, null, null, 'ACTIVE')
ON CONFLICT (role_access_id) DO UPDATE SET 
    can_view = EXCLUDED.can_view, 
    can_add = EXCLUDED.can_add, 
    can_edit = EXCLUDED.can_edit, 
    can_delete = EXCLUDED.can_delete, 
    updated_at = EXCLUDED.updated_at, 
    updated_by = EXCLUDED.updated_by, 
    status = EXCLUDED.status;