-- Allow vendors to set Registered status (status_id = 3) when creating tickets
UPDATE `Status`
SET allowed_roles = CASE
    WHEN allowed_roles IS NULL OR TRIM(allowed_roles) = '' THEN 'ROLE_VENDOR'
    WHEN allowed_roles NOT LIKE '%ROLE_VENDOR%' THEN CONCAT(allowed_roles, ',ROLE_VENDOR')
    ELSE allowed_roles
END
WHERE status_id = 3 AND status_type = 'TICKET';

UPDATE `Status`
SET allowed_roles = CASE
    WHEN allowed_roles IS NULL OR TRIM(allowed_roles) = '' THEN 'ROLE_VENDOR'
    WHEN allowed_roles NOT LIKE '%ROLE_VENDOR%' THEN CONCAT(allowed_roles, ',ROLE_VENDOR')
    ELSE allowed_roles
END
WHERE status_name IN ('Registered', 'REGISTERED') AND status_type = 'TICKET';
