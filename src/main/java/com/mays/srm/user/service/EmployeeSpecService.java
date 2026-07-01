package com.mays.srm.user.service;
import com.mays.srm.user.dto.request.EmployeeSpecRequestDTO;
import com.mays.srm.user.dto.resDTO.EmployeeSpecResponseDTO;
import java.util.List;

public interface EmployeeSpecService {
    EmployeeSpecResponseDTO create(EmployeeSpecRequestDTO requestDTO);
    List<EmployeeSpecResponseDTO> getAll();
    void delete(Integer employeeId, Integer deviceTypeId);
}
