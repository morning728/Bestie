-- Drop existing Quartz tables if exist
DROP TABLE IF EXISTS qrtz_fired_triggers;
DROP TABLE IF EXISTS qrtz_paused_trigger_grps;
DROP TABLE IF EXISTS qrtz_scheduler_state;
DROP TABLE IF EXISTS qrtz_locks;
DROP TABLE IF EXISTS qrtz_simple_triggers;
DROP TABLE IF EXISTS qrtz_simprop_triggers;
DROP TABLE IF EXISTS qrtz_cron_triggers;
DROP TABLE IF EXISTS qrtz_blob_triggers;
DROP TABLE IF EXISTS qrtz_triggers;
DROP TABLE IF EXISTS qrtz_job_details;
DROP TABLE IF EXISTS qrtz_calendars;

-- Create tables
CREATE TABLE qrtz_job_details (
                                  sched_name VARCHAR(120) NOT NULL,
                                  job_name VARCHAR(200) NOT NULL,
                                  job_group VARCHAR(200) NOT NULL,
                                  description VARCHAR(250) NULL,
                                  job_class_name VARCHAR(250) NOT NULL,
                                  is_durable BOOLEAN NOT NULL,
                                  is_nonconcurrent BOOLEAN NOT NULL,
                                  is_update_data BOOLEAN NOT NULL,
                                  requests_recovery BOOLEAN NOT NULL,
                                  job_data BYTEA NULL,
                                  PRIMARY KEY (sched_name, job_name, job_group)
);

CREATE TABLE qrtz_triggers (
                               sched_name VARCHAR(120) NOT NULL,
                               trigger_name VARCHAR(200) NOT NULL,
                               trigger_group VARCHAR(200) NOT NULL,
                               job_name VARCHAR(200) NOT NULL,
                               job_group VARCHAR(200) NOT NULL,
                               description VARCHAR(250) NULL,
                               next_fire_time BIGINT NULL,
                               prev_fire_time BIGINT NULL,
                               priority INTEGER NULL,
                               trigger_state VARCHAR(16) NOT NULL,
                               trigger_type VARCHAR(8) NOT NULL,
                               start_time BIGINT NOT NULL,
                               end_time BIGINT NULL,
                               calendar_name VARCHAR(200) NULL,
                               misfire_instr SMALLINT NULL,
                               job_data BYTEA NULL,
                               PRIMARY KEY (sched_name, trigger_name, trigger_group),
                               FOREIGN KEY (sched_name, job_name, job_group)
                                   REFERENCES qrtz_job_details(sched_name, job_name, job_group)
);

CREATE TABLE qrtz_simple_triggers (
                                      sched_name VARCHAR(120) NOT NULL,
                                      trigger_name VARCHAR(200) NOT NULL,
                                      trigger_group VARCHAR(200) NOT NULL,
                                      repeat_count BIGINT NOT NULL,
                                      repeat_interval BIGINT NOT NULL,
                                      times_triggered BIGINT NOT NULL,
                                      PRIMARY KEY (sched_name, trigger_name, trigger_group),
                                      FOREIGN KEY (sched_name, trigger_name, trigger_group)
                                          REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group)
);

CREATE TABLE qrtz_cron_triggers (
                                    sched_name VARCHAR(120) NOT NULL,
                                    trigger_name VARCHAR(200) NOT NULL,
                                    trigger_group VARCHAR(200) NOT NULL,
                                    cron_expression VARCHAR(120) NOT NULL,
                                    time_zone_id VARCHAR(80),
                                    PRIMARY KEY (sched_name, trigger_name, trigger_group),
                                    FOREIGN KEY (sched_name, trigger_name, trigger_group)
                                        REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group)
);

CREATE TABLE qrtz_simprop_triggers (
                                       sched_name VARCHAR(120) NOT NULL,
                                       trigger_name VARCHAR(200) NOT NULL,
                                       trigger_group VARCHAR(200) NOT NULL,
                                       str_prop_1 VARCHAR(512) NULL,
                                       str_prop_2 VARCHAR(512) NULL,
                                       str_prop_3 VARCHAR(512) NULL,
                                       int_prop_1 INTEGER NULL,
                                       int_prop_2 INTEGER NULL,
                                       long_prop_1 BIGINT NULL,
                                       long_prop_2 BIGINT NULL,
                                       dec_prop_1 NUMERIC(13,4) NULL,
                                       dec_prop_2 NUMERIC(13,4) NULL,
                                       bool_prop_1 BOOLEAN NULL,
                                       bool_prop_2 BOOLEAN NULL,
                                       PRIMARY KEY (sched_name, trigger_name, trigger_group),
                                       FOREIGN KEY (sched_name, trigger_name, trigger_group)
                                           REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group)
);

CREATE TABLE qrtz_blob_triggers (
                                    sched_name VARCHAR(120) NOT NULL,
                                    trigger_name VARCHAR(200) NOT NULL,
                                    trigger_group VARCHAR(200) NOT NULL,
                                    blob_data BYTEA NULL,
                                    PRIMARY KEY (sched_name, trigger_name, trigger_group),
                                    FOREIGN KEY (sched_name, trigger_name, trigger_group)
                                        REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group)
);

CREATE TABLE qrtz_calendars (
                                sched_name VARCHAR(120) NOT NULL,
                                calendar_name VARCHAR(200) NOT NULL,
                                calendar BYTEA NOT NULL,
                                PRIMARY KEY (sched_name, calendar_name)
);

CREATE TABLE qrtz_paused_trigger_grps (
                                          sched_name VARCHAR(120) NOT NULL,
                                          trigger_group VARCHAR(200) NOT NULL,
                                          PRIMARY KEY (sched_name, trigger_group)
);

CREATE TABLE qrtz_fired_triggers (
                                     sched_name VARCHAR(120) NOT NULL,
                                     entry_id VARCHAR(95) NOT NULL,
                                     trigger_name VARCHAR(200) NOT NULL,
                                     trigger_group VARCHAR(200) NOT NULL,
                                     instance_name VARCHAR(200) NOT NULL,
                                     fired_time BIGINT NOT NULL,
                                     sched_time BIGINT NOT NULL, -- <== ВОТ ЭТО ОЧЕНЬ ВАЖНО
                                     priority INTEGER NOT NULL,
                                     state VARCHAR(16) NOT NULL,
                                     job_name VARCHAR(200) NULL,
                                     job_group VARCHAR(200) NULL,
                                     is_nonconcurrent BOOLEAN NULL,
                                     requests_recovery BOOLEAN NULL,
                                     PRIMARY KEY (sched_name, entry_id)
);

CREATE TABLE qrtz_scheduler_state (
                                      sched_name VARCHAR(120) NOT NULL,
                                      instance_name VARCHAR(200) NOT NULL,
                                      last_checkin_time BIGINT NOT NULL,
                                      checkin_interval BIGINT NOT NULL,
                                      PRIMARY KEY (sched_name, instance_name)
);

CREATE TABLE qrtz_locks (
                            sched_name VARCHAR(120) NOT NULL,
                            lock_name VARCHAR(40) NOT NULL,
                            PRIMARY KEY (sched_name, lock_name)
);
