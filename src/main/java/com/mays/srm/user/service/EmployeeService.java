package com.mays.srm.user.service;
import com.mays.srm.service.GenericService;
import com.mays.srm.user.dto.request.EmployeeRequestDTO;
import com.mays.srm.user.dto.resDTO.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService extends GenericService<EmployeeRequestDTO, EmployeeResponseDTO, Integer> {
    List<EmployeeResponseDTO> getEmployeesByDepartmentId(Integer departmentId);
    void deleteEmployees(List<Integer> ids); // New method for multiple deletions
    void validateMobileNumber(String mobileNo, Integer currentEmployeeId, Integer currentUserId);
}
