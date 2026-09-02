package com.mays.srm.enquiry.service.impl;

import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.Device;
import com.mays.srm.device.entities.DeviceModel;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.device.repository.BrandDao;
import com.mays.srm.device.repository.DeviceDao;
import com.mays.srm.device.repository.DeviceModelDao;
import com.mays.srm.device.repository.DeviceTypeDao;
import com.mays.srm.enquiry.dto.request.InwardRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.InwardResponseDTO;
import com.mays.srm.enquiry.entities.InwardRecord;
import com.mays.srm.enquiry.repository.InwardRecordDao;
import com.mays.srm.enquiry.service.InwardService;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.user.entities.UserEntryReport;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.UserEntryReportDao;
import com.mays.srm.user.repository.UserMasterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InwardServiceImpl implements InwardService {

    @Autowired
    private InwardRecordDao repository;

    @Autowired
    private UserMasterDao userMasterDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceTypeDao deviceTypeDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private DeviceModelDao deviceModelDao;

    @Autowired
    private UserEntryReportDao userEntryReportDao;

    @Override
    @Transactional
    public InwardResponseDTO createInward(InwardRequestDTO requestDTO) {
        // 1. Resolve UserMaster
        UserMaster customer = null;
        if (requestDTO.getUserId() != null) {
            customer = userMasterDao.findById(requestDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + requestDTO.getUserId()));
        } else if (requestDTO.getMobileNo() != null && !requestDTO.getMobileNo().trim().isEmpty()) {
            Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(requestDTO.getMobileNo().trim());
            if (userOpt.isPresent()) {
                customer = userOpt.get();
            } else {
                customer = new UserMaster();
                String fullName = requestDTO.getCustomerName() != null ? requestDTO.getCustomerName().trim() : "Inward Customer";
                String[] nameParts = fullName.split("\\s+");
                customer.setFirstName(nameParts[0]);
                customer.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                customer.setMobileNo(requestDTO.getMobileNo().trim());
                customer.setEmailId(requestDTO.getEmailId() != null ? requestDTO.getEmailId().trim() : null);
                customer.setAddress(requestDTO.getAddress());
                customer.setRole("ROLE_USER");
                customer.setIsActive(true);
                customer = userMasterDao.save(customer);
            }
        }

        if (customer == null) {
            throw new IllegalArgumentException("Cannot create Inward without customer details or registered user.");
        }

        // 2. Resolve/Create Device
        Device device = null;
        if (requestDTO.getSerialNo() != null && !requestDTO.getSerialNo().trim().isEmpty()) {
            String serialNo = requestDTO.getSerialNo().trim();
            Optional<Device> deviceOpt = deviceDao.findById(serialNo);
            if (deviceOpt.isPresent()) {
                device = deviceOpt.get();
            } else {
                device = new Device();
                device.setSerialNo(serialNo);
                
                // Set DeviceModel if modelId or customModelName is provided
                DeviceModel resolvedModel = null;
                if (requestDTO.getModelId() != null) {
                    resolvedModel = deviceModelDao.findById(requestDTO.getModelId()).orElse(null);
                } else if (requestDTO.getCustomModelName() != null && !requestDTO.getCustomModelName().trim().isEmpty() && requestDTO.getBrandId() != null) {
                    String customName = requestDTO.getCustomModelName().trim();
                    Optional<DeviceModel> existingModelOpt = deviceModelDao
                            .findByModelNameIgnoreCaseAndBrandBrandId(customName, requestDTO.getBrandId());
                    if (existingModelOpt.isPresent()) {
                        resolvedModel = existingModelOpt.get();
                    } else {
                        Brand brand = brandDao.findById(requestDTO.getBrandId()).orElse(null);
                        if (brand != null) {
                            DeviceModel newModel = new DeviceModel();
                            newModel.setModelName(customName);
                            newModel.setBrand(brand);
                            newModel.setModelDescription("Custom created model from Inward");
                            resolvedModel = deviceModelDao.save(newModel);
                        }
                    }
                }
                
                if (resolvedModel != null) {
                    device.setModel(resolvedModel);
                }
                device = deviceDao.save(device);
            }
        }

        // 3. Save InwardRecord
        InwardRecord inward = new InwardRecord();
        inward.setUser(customer);
        inward.setSerialNo(requestDTO.getSerialNo());
        inward.setInwardRemarks(requestDTO.getInwardRemarks());
        inward.setCreatedByEmployeeId(requestDTO.getCreatedByEmployeeId());

        if (requestDTO.getDeviceTypeId() != null) {
            deviceTypeDao.findById(requestDTO.getDeviceTypeId()).ifPresent(inward::setDeviceType);
        }
        if (requestDTO.getBrandId() != null) {
            brandDao.findById(requestDTO.getBrandId()).ifPresent(inward::setBrand);
        }
        if (requestDTO.getModelId() != null) {
            deviceModelDao.findById(requestDTO.getModelId()).ifPresent(inward::setDeviceModel);
        } else if (requestDTO.getCustomModelName() != null) {
            inward.setCustomModelName(requestDTO.getCustomModelName());
        }

        InwardRecord savedInward = repository.save(inward);

        // 4. Log UserEntryReport automatically
        UserEntryReport report = new UserEntryReport();
        report.setUser(customer);
        report.setReason("Inward");
        report.setEntryType("INWARD");
        report.setInward(savedInward);
        userEntryReportDao.save(report);

        return mapToResponse(savedInward);
    }

    @Override
    public List<InwardResponseDTO> getAllInwards() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public InwardResponseDTO getInwardById(Integer id) {
        InwardRecord record = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inward record not found with ID: " + id));
        return mapToResponse(record);
    }

    private InwardResponseDTO mapToResponse(InwardRecord record) {
        InwardResponseDTO dto = new InwardResponseDTO();
        dto.setInwardId(record.getInwardId());
        dto.setUserId(record.getUser().getUserId());
        dto.setUserName(record.getUser().getFirstName() + " " + record.getUser().getLastName());
        dto.setSerialNo(record.getSerialNo());
        dto.setInwardRemarks(record.getInwardRemarks());
        dto.setCreatedByEmployeeId(record.getCreatedByEmployeeId());
        dto.setCreatedDate(record.getCreatedDate());

        if (record.getDeviceType() != null) {
            dto.setDeviceTypeId(record.getDeviceType().getDeviceTypeId());
            dto.setDeviceTypeName(record.getDeviceType().getDeviceTypeName());
        }
        if (record.getBrand() != null) {
            dto.setBrandId(record.getBrand().getBrandId());
            dto.setBrandName(record.getBrand().getBrandName());
        }
        if (record.getDeviceModel() != null) {
            dto.setModelId(record.getDeviceModel().getModelId());
            dto.setDeviceModelName(record.getDeviceModel().getModelName());
        } else {
            dto.setCustomModelName(record.getCustomModelName());
        }

        return dto;
    }
}
