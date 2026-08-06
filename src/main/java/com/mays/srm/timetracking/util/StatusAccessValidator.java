package com.mays.srm.timetracking.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;

import com.mays.srm.organization.entities.Status;
import com.mays.srm.security.core.CustomUserDetails;
import com.mays.srm.security.util.SecurityUtils;

public final class StatusAccessValidator {

    private StatusAccessValidator() {
    }

    public static void validateStatusAccess(Status status) {
        if (status == null) {
            return;
        }
        String allowedRoles = status.getAllowedRoles();
        if (allowedRoles == null || allowedRoles.isBlank()) {
            return;
        }
        CustomUserDetails user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Authentication required."));
        String userRole = user.getAuthorities().iterator().next().getAuthority();
        Set<String> allowed = Arrays.stream(allowedRoles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        if (!allowed.isEmpty() && !allowed.contains(userRole)) {
            throw new AccessDeniedException("Your role cannot set status: " + status.getStatusName());
        }
    }

    public static boolean isExecutiveOrManager() {
        return SecurityUtils.getCurrentUser()
                .map(u -> {
                    String role = u.getAuthorities().iterator().next().getAuthority();
                    return "ROLE_EXECUTIVE".equals(role) || "ROLE_MANAGER".equals(role) || "ROLE_ADMIN".equals(role);
                })
                .orElse(false);
    }

    public static Integer getCurrentEmployeeId() {
        return SecurityUtils.getCurrentUser()
                .filter(u -> {
                    String role = u.getAuthorities().iterator().next().getAuthority();
                    return !"ROLE_USER".equals(role) && !"ROLE_VENDOR".equals(role);
                })
                .map(CustomUserDetails::getUserId)
                .orElse(null);
    }
}
