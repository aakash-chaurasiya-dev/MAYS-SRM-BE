package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.EmployeeSpecRequestDTO;
import com.mays.srm.dto.responseDTO.EmployeeSpecResponseDTO;
import java.util.List;

public interface EmployeeSpecService {
    EmployeeSpecResponseDTO create(EmployeeSpecRequestDTO requestDTO);
    List<EmployeeSpecResponseDTO> getAll();
    void delete(Integer employeeId, Integer deviceTypeId);
}
