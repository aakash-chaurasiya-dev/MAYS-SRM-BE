package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DeviceDao;
import com.mays.srm.dao.core.DeviceModelDao;
import com.mays.srm.entity.Device;
import com.mays.srm.entity.DeviceModel;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceDao deviceDao;
    private final DeviceModelDao deviceModelDao;

    @Autowired
    public DeviceServiceImpl(DeviceDao deviceDao, DeviceModelDao deviceModelDao) {
        this.deviceDao = deviceDao;
        this.deviceModelDao = deviceModelDao;
    }

    @Override
    public Device create(Device device) {
        try {
            if (device.getModel() != null) {
                DeviceModel inputModel = device.getModel();

                // Expecting existing model using ID
                if (inputModel.getModelId() != null) {
                    Optional<DeviceModel> modelOpt = deviceModelDao.findById(inputModel.getModelId());
                    if (modelOpt.isPresent()) {
                        device.setModel(modelOpt.get());
                    } else {
                        throw new ResourceNotFoundException("DeviceModel not found with ID: " + inputModel.getModelId());
                    }
                } 
                else {
                    throw new BadRequestException("Valid DeviceModel ID must be provided to create a device.");
                }
            } else {
                throw new BadRequestException("Model information is required to create a device.");
            }

            return deviceDao.save(device);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Device", ex);
        }
    }

    @Override
    public Optional<Device> getById(String id) {
        Optional<Device> deviceOpt = deviceDao.findById(id);
        
        if (deviceOpt.isPresent()) {
            return deviceOpt;
        } else {
            throw new ResourceNotFoundException("Device not found with serial number: " + id);
        }
    }

    @Override
    public List<Device> getAll() {
        return deviceDao.findAll();
    }

    @Override
    public Device update(Device device) {
        try {
            if (device.getSerialNo() == null) {
                throw new ResourceNotFoundException("Cannot update. Device serial number is missing.");
            }
            
            boolean exists = deviceDao.existsById(device.getSerialNo());
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. Device not found with serial number: " + device.getSerialNo());
            }

            if (device.getModel() != null) {
                DeviceModel inputModel = device.getModel();

                // Expecting existing model using ID
                if (inputModel.getModelId() != null) {
                    Optional<DeviceModel> modelOpt = deviceModelDao.findById(inputModel.getModelId());
                    if (modelOpt.isPresent()) {
                        device.setModel(modelOpt.get());
                    } else {
                        throw new ResourceNotFoundException("DeviceModel not found with ID: " + inputModel.getModelId());
                    }
                } else {
                    throw new BadRequestException("Valid DeviceModel ID must be provided to update a device.");
                }
            } else {
                throw new BadRequestException("Model information is required to update a device.");
            }

            return deviceDao.save(device);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Device", ex);
        }
    }

    @Override
    public void delete(String id) {
        boolean exists = deviceDao.existsById(id);
        
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. Device not found with serial number: " + id);
        }
        
        try {
            deviceDao.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Device because it is assigned to existing tickets or other records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Device with serial number: " + id, ex);
        }
    }


    @Override
    public List<Device> findByModelName(String modelName) {
        List<Device> devices = deviceDao.findByModelName(modelName);
        if (devices.isEmpty()) {
            throw new ResourceNotFoundException("No devices found with model name: " + modelName);
        }
        return devices;
    }

    @Override
    public List<Device> findByBrandName(String brandName) {
        List<Device> devices = deviceDao.findByBrandName(brandName);
        if (devices.isEmpty()) {
            throw new ResourceNotFoundException("No devices found with brand name: " + brandName);
        }
        return devices;
    }

    @Override
    public List<Device> findByDeviceTypeName(String deviceType) {
        List<Device> devices = deviceDao.findByDeviceTypeName(deviceType);
        if (devices.isEmpty()) {
            throw new ResourceNotFoundException("No devices found with device type: " + deviceType);
        }
        return devices;
    }
}
