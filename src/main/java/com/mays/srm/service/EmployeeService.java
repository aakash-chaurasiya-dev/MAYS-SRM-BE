package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.EmployeeRequestDTO;
import com.mays.srm.dto.responseDTO.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService extends GenericService<EmployeeRequestDTO, EmployeeResponseDTO, Integer> {
    List<EmployeeResponseDTO> getEmployeesByDepartmentId(Integer departmentId);
}
