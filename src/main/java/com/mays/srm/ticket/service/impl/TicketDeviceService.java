package com.mays.srm.ticket.service.impl;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.Device;
import com.mays.srm.device.entities.DeviceModel;
import com.mays.srm.device.repository.BrandDao;
import com.mays.srm.device.repository.DeviceDao;
import com.mays.srm.device.repository.DeviceModelDao;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketDeviceService {

    private final DeviceModelDao deviceModelDao;
    private final BrandDao brandDao;
    private final DeviceDao deviceDao;

    @Autowired
    public TicketDeviceService(DeviceModelDao deviceModelDao, BrandDao brandDao, DeviceDao deviceDao) {
        this.deviceModelDao = deviceModelDao;
        this.brandDao = brandDao;
        this.deviceDao = deviceDao;
    }

    /**
     * Resolves the device model and device instance for a ticket creation/update
     */
    public void handleDeviceCreation(Ticket ticket, TicketRequestDTO requestDTO) {
        if (requestDTO.getDeviceSerialNo() != null) { 
            // 1. Resolve DeviceModel
            DeviceModel resolvedModel = null;
            if (requestDTO.getDeviceModelId() != null) {
                resolvedModel = deviceModelDao.findById(requestDTO.getDeviceModelId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Device Model not found with ID: " + requestDTO.getDeviceModelId()));
            } else if (requestDTO.getCustomModelName() != null && !requestDTO.getCustomModelName().trim().isEmpty()
                    && requestDTO.getBrandId() != null) {
                String customName = requestDTO.getCustomModelName().trim();
                Optional<DeviceModel> existingModelOpt = deviceModelDao
                        .findByModelNameIgnoreCaseAndBrandBrandId(customName, requestDTO.getBrandId());
                if (existingModelOpt.isPresent()) {
                    resolvedModel = existingModelOpt.get();
                } else {
                    // Create new DeviceModel
                    Brand brand = brandDao.findById(requestDTO.getBrandId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Brand not found with ID: " + requestDTO.getBrandId()));
                    DeviceModel newModel = new DeviceModel();
                    newModel.setModelName(customName);
                    newModel.setBrand(brand);
                    newModel.setModelDescription("Custom created model");
                    resolvedModel = deviceModelDao.save(newModel);
                }
            }

            // 2. Resolve/Update Device
            Optional<Device> deviceOpt = deviceDao.findById(requestDTO.getDeviceSerialNo());
            Device device;
            if (deviceOpt.isPresent()) {
                device = deviceOpt.get();
            } else {
                device = new Device();
                device.setSerialNo(requestDTO.getDeviceSerialNo());
            }

            if (resolvedModel != null) {
                device.setModel(resolvedModel);
            }

            ticket.setDevice(deviceDao.save(device));
        }
    }
}
