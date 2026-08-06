-- =============================================================================
-- MAYS SRM: SLA Time Tracking & Hold Request Migration
-- Run this script against your MySQL database BEFORE starting the backend.
-- =============================================================================

-- 1. Extend Status table with sla_timer_action
ALTER TABLE `Status`
    ADD COLUMN IF NOT EXISTS `sla_timer_action` VARCHAR(30) NOT NULL DEFAULT 'NONE'
    COMMENT 'NONE | CREATE_HOLD_REQUEST | PAUSE_TIMER | RESUME_TIMER | STOP_TIMER';

-- 2. SLA Policy table (department-wise SLA limits)
CREATE TABLE IF NOT EXISTS `sla_policy` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `department_id` INT NOT NULL,
    `role` VARCHAR(50) NULL COMMENT 'Optional role override e.g. ROLE_ENGINEER',
    `target_minutes` INT NOT NULL DEFAULT 120,
    `is_timer_tracked` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '0 = Sales/Executive - no SLA timer',
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_sla_policy_dept` (`department_id`),
    CONSTRAINT `fk_sla_policy_department` FOREIGN KEY (`department_id`) REFERENCES `Department` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. SLA Hold Request table (approval workflow)
CREATE TABLE IF NOT EXISTS `sla_hold_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `ticket_id` INT NOT NULL,
    `tracking_id` BIGINT NOT NULL,
    `requested_by` INT NOT NULL,
    `reason` TEXT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING|APPROVED|REJECTED|CANCELLED|RELEASED',
    `requested_at` DATETIME NOT NULL,
    `reviewed_by` INT NULL,
    `reviewed_at` DATETIME NULL,
    `review_remark` TEXT NULL,
    `released_at` DATETIME NULL,
    PRIMARY KEY (`id`),
    KEY `idx_hold_ticket` (`ticket_id`),
    KEY `idx_hold_status` (`status`),
    CONSTRAINT `fk_hold_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `Ticket` (`ticket_id`),
    CONSTRAINT `fk_hold_tracking` FOREIGN KEY (`tracking_id`) REFERENCES `ticket_time_tracking` (`id`),
    CONSTRAINT `fk_hold_requested_by` FOREIGN KEY (`requested_by`) REFERENCES `Employee` (`employee_id`),
    CONSTRAINT `fk_hold_reviewed_by` FOREIGN KEY (`reviewed_by`) REFERENCES `Employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 4. Seed SLA Policies (adjust department_id values to match YOUR database)
--    Run: SELECT department_id, department_name, default_role FROM Department;
-- =============================================================================

-- Example: Engineering dept (240 min for engineers)
-- INSERT INTO sla_policy (department_id, role, target_minutes, is_timer_tracked, is_active)
-- SELECT department_id, 'ROLE_ENGINEER', 240, 1, 1 FROM Department WHERE department_name LIKE '%Engineer%' LIMIT 1;

-- Example: Sales dept (no timer tracking)
-- INSERT INTO sla_policy (department_id, role, target_minutes, is_timer_tracked, is_active)
-- SELECT department_id, NULL, 120, 0, 1 FROM Department WHERE department_name LIKE '%Sales%' LIMIT 1;

-- Default fallback for all departments (customize as needed):
-- INSERT INTO sla_policy (department_id, role, target_minutes, is_timer_tracked, is_active)
-- SELECT department_id, NULL, 120, 1, 1 FROM Department;

-- =============================================================================
-- 5. Seed / Update Ticket Statuses with sla_timer_action
--    Adjust status_name matching to your existing Status rows.
-- =============================================================================

-- Request for Hold (engineer can set) - creates pending hold request, timer keeps running
INSERT INTO `Status` (status_name, status_flg, status_description, status_type, is_locked, sla_timer_action, allowed_roles)
SELECT 'Request for Hold', 1, 'Engineer requests SLA hold approval', 'TICKET', 0, 'CREATE_HOLD_REQUEST', 'ROLE_ENGINEER'
WHERE NOT EXISTS (SELECT 1 FROM `Status` WHERE status_name = 'Request for Hold' AND status_type = 'TICKET');

UPDATE `Status` SET sla_timer_action = 'CREATE_HOLD_REQUEST',
    allowed_roles = 'ROLE_ENGINEER',
    status_description = 'Engineer requests SLA hold approval'
WHERE status_name = 'Request for Hold' AND status_type = 'TICKET';

-- HOLD (executive only) - pauses SLA timer
UPDATE `Status` SET sla_timer_action = 'PAUSE_TIMER',
    allowed_roles = 'ROLE_EXECUTIVE,ROLE_MANAGER,ROLE_ADMIN',
    status_description = 'SLA timer paused - hold approved'
WHERE status_name = 'HOLD' AND status_type = 'TICKET';

-- If HOLD status does not exist yet:
INSERT INTO `Status` (status_name, status_flg, status_description, status_type, is_locked, sla_timer_action, allowed_roles)
SELECT 'HOLD', 1, 'SLA timer paused - hold approved', 'TICKET', 0, 'PAUSE_TIMER', 'ROLE_EXECUTIVE,ROLE_MANAGER,ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM `Status` WHERE status_name = 'HOLD' AND status_type = 'TICKET');

-- Hold-Released (executive only) - resumes SLA timer
INSERT INTO `Status` (status_name, status_flg, status_description, status_type, is_locked, sla_timer_action, allowed_roles)
SELECT 'Hold-Released', 1, 'SLA hold released - timer resumes', 'TICKET', 0, 'RESUME_TIMER', 'ROLE_EXECUTIVE,ROLE_MANAGER,ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM `Status` WHERE status_name = 'Hold-Released' AND status_type = 'TICKET');

UPDATE `Status` SET sla_timer_action = 'RESUME_TIMER',
    allowed_roles = 'ROLE_EXECUTIVE,ROLE_MANAGER,ROLE_ADMIN'
WHERE status_name = 'Hold-Released' AND status_type = 'TICKET';

-- CLOSED - stops SLA timer permanently
UPDATE `Status` SET sla_timer_action = 'STOP_TIMER'
WHERE status_name IN ('CLOSED', 'Closed') AND status_type = 'TICKET';

-- In Progress / Open - no timer action
UPDATE `Status` SET sla_timer_action = 'NONE'
WHERE status_name IN ('In Progress', 'IN PROGRESS', 'Open', 'OPEN') AND status_type = 'TICKET'
  AND (sla_timer_action IS NULL OR sla_timer_action = '');

-- =============================================================================
-- 6. Verify
-- =============================================================================
-- SELECT status_id, status_name, status_type, sla_timer_action, allowed_roles FROM Status WHERE status_type = 'TICKET';
-- SELECT * FROM sla_policy;
-- DESCRIBE sla_hold_request;
