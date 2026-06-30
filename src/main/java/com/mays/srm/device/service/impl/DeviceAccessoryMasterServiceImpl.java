package com.mays.srm.device.service.impl;

import com.mays.srm.device.repository.DeviceTypeDao;
import com.mays.srm.device.dto.request.DeviceAccessoryMasterRequestDTO;
import com.mays.srm.device.dto.resDTO.DeviceAccessoryMasterResponseDTO;
import com.mays.srm.device.entities.DeviceAccessoryMaster;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.device.repository.DeviceAccessoryMasterDao;
import com.mays.srm.device.service.DeviceAccessoryMasterService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceAccessoryMasterServiceImpl implements DeviceAccessoryMasterService {

    private final DeviceAccessoryMasterDao dao;
    private final DeviceTypeDao deviceTypeDao;
    private final ModelMapper modelMapper;

    public DeviceAccessoryMasterServiceImpl(DeviceAccessoryMasterDao dao, DeviceTypeDao deviceTypeDao, ModelMapper modelMapper) {
        this.dao = dao;
        this.deviceTypeDao = deviceTypeDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public DeviceAccessoryMasterResponseDTO create(DeviceAccessoryMasterRequestDTO requestDTO) {
        DeviceType deviceType = deviceTypeDao.findById(requestDTO.getDeviceTypeId())
                .orElseThrow(() -> new RuntimeException("DeviceType not found with id: " + requestDTO.getDeviceTypeId()));

        DeviceAccessoryMaster entity = modelMapper.map(requestDTO, DeviceAccessoryMaster.class);
        entity.setDeviceType(deviceType);
        
        DeviceAccessoryMaster savedEntity = dao.save(entity);
        return mapToResponse(savedEntity);
    }

    @Override
    public DeviceAccessoryMasterResponseDTO getById(Integer id) {
        DeviceAccessoryMaster entity = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("DeviceAccessoryMaster not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    public List<DeviceAccessoryMasterResponseDTO> getAll() {
        return dao.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceAccessoryMasterResponseDTO> getByDeviceTypeId(Integer deviceTypeId) {
        return dao.findByDeviceType_DeviceTypeId(deviceTypeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeviceAccessoryMasterResponseDTO update(Integer id, DeviceAccessoryMasterRequestDTO requestDTO) {
        DeviceAccessoryMaster entity = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("DeviceAccessoryMaster not found with id: " + id));

        if (!entity.getDeviceType().getDeviceTypeId().equals(requestDTO.getDeviceTypeId())) {
            DeviceType deviceType = deviceTypeDao.findById(requestDTO.getDeviceTypeId())
                    .orElseThrow(() -> new RuntimeException("DeviceType not found with id: " + requestDTO.getDeviceTypeId()));
            entity.setDeviceType(deviceType);
        }

        entity.setAccessoryName(requestDTO.getAccessoryName());
        entity.setDescription(requestDTO.getDescription());

        DeviceAccessoryMaster updatedEntity = dao.save(entity);
        return mapToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        DeviceAccessoryMaster entity = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("DeviceAccessoryMaster not found with id: " + id));
        dao.delete(entity);
    }

    private DeviceAccessoryMasterResponseDTO mapToResponse(DeviceAccessoryMaster entity) {
        DeviceAccessoryMasterResponseDTO responseDTO = modelMapper.map(entity, DeviceAccessoryMasterResponseDTO.class);
        if (entity.getDeviceType() != null) {
            responseDTO.setDeviceTypeId(entity.getDeviceType().getDeviceTypeId());
            responseDTO.setDeviceTypeName(entity.getDeviceType().getDeviceTypeName());
        }
        return responseDTO;
    }
}
