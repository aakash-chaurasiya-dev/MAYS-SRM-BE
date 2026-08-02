package com.mays.srm.organization.util;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.organization.entities.Department;
import com.mays.srm.organization.repository.DepartmentDao;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DepartmentRoleResolver {

    public static final List<String> KNOWN_ROLES = List.of(
            "ROLE_MANAGER",
            "ROLE_EXECUTIVE",
            "ROLE_ENGINEER",
            "ROLE_PURCHASE",
            "ROLE_ADMIN",
            "ROLE_USER",
            "ROLE_VENDOR"
    );

    private final DepartmentDao departmentDao;

    public DepartmentRoleResolver(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public String resolveRole(Department department) {
        if (department == null) {
            throw new BadRequestException("Department not specified or not found.");
        }
        String role = department.getDefaultRole();
        if (role == null || role.trim().isEmpty()) {
            throw new BadRequestException(
                    "Department '" + department.getDepartmentName()
                            + "' has no default role configured. Set Default Role in Department Maintenance.");
        }
        return role.trim();
    }

    public String resolveAllowedRolesCsv(String allowedDepartmentIds) {
        if (allowedDepartmentIds == null || allowedDepartmentIds.trim().isEmpty()) {
            return null;
        }
        Set<String> roles = new LinkedHashSet<>();
        for (String idStr : allowedDepartmentIds.split(",")) {
            String trimmed = idStr.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                Integer deptId = Integer.parseInt(trimmed);
                Optional<Department> deptOpt = departmentDao.findById(deptId);
                if (deptOpt.isPresent()) {
                    String role = deptOpt.get().getDefaultRole();
                    if (role != null && !role.trim().isEmpty()) {
                        roles.add(role.trim());
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }
        if (roles.isEmpty()) {
            return null;
        }
        return String.join(",", new ArrayList<>(roles));
    }

    public void validateKnownRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new BadRequestException("Default role is required for a department.");
        }
        String normalized = role.trim();
        if (!KNOWN_ROLES.contains(normalized)) {
            throw new BadRequestException(
                    "Unknown role '" + normalized + "'. Allowed: " + String.join(", ", KNOWN_ROLES));
        }
    }
}
