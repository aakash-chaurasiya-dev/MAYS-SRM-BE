package com.mays.srm.timetracking.service;

import java.util.List;

import com.mays.srm.timetracking.dto.request.SlaPolicyRequestDTO;
import com.mays.srm.timetracking.dto.resDTO.SlaPolicyResponseDTO;

public interface SlaPolicyService {

    SlaPolicyResponseDTO create(SlaPolicyRequestDTO requestDTO);

    SlaPolicyResponseDTO update(Long id, SlaPolicyRequestDTO requestDTO);

    void delete(Long id);

    List<SlaPolicyResponseDTO> getAll();

    SlaPolicyResponseDTO getById(Long id);
}
