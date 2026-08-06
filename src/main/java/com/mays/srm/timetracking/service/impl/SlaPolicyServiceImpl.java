package com.mays.srm.timetracking.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.organization.entities.Department;
import com.mays.srm.organization.repository.DepartmentDao;
import com.mays.srm.timetracking.dto.request.SlaPolicyRequestDTO;
import com.mays.srm.timetracking.dto.resDTO.SlaPolicyResponseDTO;
import com.mays.srm.timetracking.entities.SlaPolicy;
import com.mays.srm.timetracking.repository.SlaPolicyRepository;
import com.mays.srm.timetracking.service.SlaPolicyService;

@Service
public class SlaPolicyServiceImpl implements SlaPolicyService {

    @Autowired
    private SlaPolicyRepository slaPolicyRepository;

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    public SlaPolicyResponseDTO create(SlaPolicyRequestDTO requestDTO) {
        SlaPolicy policy = mapRequestToEntity(new SlaPolicy(), requestDTO);
        return mapToDTO(slaPolicyRepository.save(policy));
    }

    @Override
    public SlaPolicyResponseDTO update(Long id, SlaPolicyRequestDTO requestDTO) {
        SlaPolicy existing = slaPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SLA policy not found: " + id));
        mapRequestToEntity(existing, requestDTO);
        return mapToDTO(slaPolicyRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!slaPolicyRepository.existsById(id)) {
            throw new ResourceNotFoundException("SLA policy not found: " + id);
        }
        slaPolicyRepository.deleteById(id);
    }

    @Override
    public List<SlaPolicyResponseDTO> getAll() {
        List<SlaPolicyResponseDTO> list = new ArrayList<>();
        for (SlaPolicy policy : slaPolicyRepository.findAll()) {
            list.add(mapToDTO(policy));
        }
        return list;
    }

    @Override
    public SlaPolicyResponseDTO getById(Long id) {
        return slaPolicyRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("SLA policy not found: " + id));
    }

    private SlaPolicy mapRequestToEntity(SlaPolicy policy, SlaPolicyRequestDTO dto) {
        if (dto.getDepartmentId() == null) {
            throw new InternalServerException("departmentId is required");
        }
        Department dept = departmentDao.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.getDepartmentId()));
        policy.setDepartment(dept);
        policy.setRole(dto.getRole());
        policy.setTargetMinutes(dto.getTargetMinutes() != null ? dto.getTargetMinutes() : 120);
        policy.setIsTimerTracked(dto.getIsTimerTracked() != null ? dto.getIsTimerTracked() : true);
        policy.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return policy;
    }

    private SlaPolicyResponseDTO mapToDTO(SlaPolicy policy) {
        SlaPolicyResponseDTO dto = new SlaPolicyResponseDTO();
        dto.setId(policy.getId());
        if (policy.getDepartment() != null) {
            dto.setDepartmentId(policy.getDepartment().getDepartmentId());
            dto.setDepartmentName(policy.getDepartment().getDepartmentName());
        }
        dto.setRole(policy.getRole());
        dto.setTargetMinutes(policy.getTargetMinutes());
        dto.setIsTimerTracked(policy.getIsTimerTracked());
        dto.setIsActive(policy.getIsActive());
        return dto;
    }
}
