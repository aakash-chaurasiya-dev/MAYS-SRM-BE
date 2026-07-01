package com.mays.srm.security;
import com.mays.srm.user.entities.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// STEP 1: THE ID CARD (Passport)
// Spring Security ONLY understands "UserDetails". 
// We create this class to wrap our Employee or User data into a format Spring understands.
public class CustomUserDetails implements UserDetails {

    private final String mobileNo;
    private final String password;
    private final String role;
    private final boolean isActive;
    private final String name;
    private final Integer userId;

    // Constructor to build our "User/employee"
    public CustomUserDetails(String mobileNo, String password, String role, boolean isActive, String name, Integer userId) {
        this.mobileNo = mobileNo;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public Integer getUserId() {
        return userId;
    }

    // Spring asks: What are this user's roles?
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    // Spring asks: What is their password? (Used for checking during login)
    @Override
    public String getPassword() {
        return password;
    }

    // Spring asks: What is their main identifier? (Usually username, here we use mobileNo)
    @Override
    public String getUsername() {
        return mobileNo;
    }

    // Spring asks: Is their account active/unlocked?
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return isActive; } // If isActive is false, they can't login!
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return isActive; }
}
