package com.mays.srm.security.util;

import com.mays.srm.security.core.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<CustomUserDetails> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(details);
        }
        return Optional.empty();
    }

    public static Optional<Integer> getCurrentVendorId() {
        return getCurrentUser()
                .filter(user -> "ROLE_VENDOR".equals(user.getAuthorities().iterator().next().getAuthority()))
                .map(CustomUserDetails::getUserId);
    }

    public static void requireVendorAccess(Integer vendorId) {
        getCurrentVendorId().ifPresent(currentVendorId -> {
            if (!currentVendorId.equals(vendorId)) {
                throw new AccessDeniedException("Vendors can only access their own data.");
            }
        });
    }
}
