package com.mays.srm.timetracking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mays.srm.organization.entities.Department;
import com.mays.srm.timetracking.entities.SlaPolicy;
import com.mays.srm.timetracking.repository.SlaPolicyRepository;
import com.mays.srm.timetracking.service.SlaCalculationService;
import com.mays.srm.user.entities.Employee;

@Service
public class SlaCalculationServiceImpl implements SlaCalculationService {

    @Autowired
    private SlaPolicyRepository slaPolicyRepository;

    @Value("${sla.default-target-minutes:120}")
    private int defaultTargetMinutes;

    @Override
    public int resolveTargetMinutes(Employee assignee) {
        if (assignee == null) {
            return defaultTargetMinutes;
        }
        SlaPolicy policy = resolvePolicy(assignee);
        if (policy != null && policy.getTargetMinutes() != null) {
            return policy.getTargetMinutes();
        }
        return defaultTargetMinutes;
    }

    @Override
    public boolean isTimerTracked(Employee assignee) {
        if (assignee == null) {
            return false;
        }
        SlaPolicy policy = resolvePolicy(assignee);
        if (policy != null) {
            return Boolean.TRUE.equals(policy.getIsTimerTracked());
        }
        return true;
    }

    private SlaPolicy resolvePolicy(Employee assignee) {
        Department department = assignee.getDepartment();
        if (department == null || department.getDepartmentId() == null) {
            return null;
        }
        Integer deptId = department.getDepartmentId();
        String role = assignee.getRole();
        if (role != null && !role.isBlank()) {
            var rolePolicy = slaPolicyRepository
                    .findFirstByDepartmentDepartmentIdAndRoleAndIsActiveTrue(deptId, role.trim());
            if (rolePolicy.isPresent()) {
                return rolePolicy.get();
            }
        }
        return slaPolicyRepository.findFirstByDepartmentDepartmentIdAndRoleIsNullAndIsActiveTrue(deptId).orElse(null);
    }
}
