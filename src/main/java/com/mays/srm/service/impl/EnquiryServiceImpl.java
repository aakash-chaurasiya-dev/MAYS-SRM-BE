package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.EnquiryDao;
import com.mays.srm.dao.core.StatusDao;
import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.dto.requestDTO.EnquiryRequestDTO;
import com.mays.srm.dto.responseDTO.EnquiryResponseDTO;
import com.mays.srm.entity.Brand;
import com.mays.srm.entity.Enquiry;
import com.mays.srm.entity.Status;
import com.mays.srm.entity.UserMaster;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.EnquiryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EnquiryServiceImpl implements EnquiryService {

    private final EnquiryDao repository;
    private final UserMasterDao userMasterDao;
    private final BrandDao brandDao;
    private final StatusDao statusDao;
    private final ModelMapper modelMapper;

    @Autowired
    public EnquiryServiceImpl(EnquiryDao repository, UserMasterDao userMasterDao, BrandDao brandDao, StatusDao statusDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.userMasterDao = userMasterDao;
        this.brandDao = brandDao;
        this.statusDao = statusDao;
        this.modelMapper = modelMapper;
    }

    @Override
    public EnquiryResponseDTO create(EnquiryRequestDTO requestDTO) {
        try {
            Enquiry enquiry = modelMapper.map(requestDTO, Enquiry.class);
            enquiry.setTimestamp(LocalDateTime.now());
            validateAndSetRelations(enquiry, requestDTO);
            
            Enquiry savedEnquiry = repository.save(enquiry);
            return mapToResponseDTO(savedEnquiry);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Enquiry", ex);
        }
    }

    @Override
    public EnquiryResponseDTO getById(Integer id) {
        Optional<Enquiry> enquiryOpt = repository.findById(id);
        if (enquiryOpt.isPresent()) {
            return mapToResponseDTO(enquiryOpt.get());
        } else {
            throw new ResourceNotFoundException("Enquiry not found with ID: " + id);
        }
    }

    @Override
    public List<EnquiryResponseDTO> getAll() {
        List<Enquiry> enquiryList = repository.findAll();
        List<EnquiryResponseDTO> dtoList = new ArrayList<>();
        for (Enquiry enquiry : enquiryList) {
            dtoList.add(mapToResponseDTO(enquiry));
        }
        return dtoList;
    }

    @Override
    public EnquiryResponseDTO update(Integer id, EnquiryRequestDTO requestDTO) {
        Optional<Enquiry> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Enquiry not found with ID: " + id);
        }
        
        Enquiry existingEnquiry = existingOpt.get();
        modelMapper.map(requestDTO, existingEnquiry);
        existingEnquiry.setEnquiryId(id);

        try {
            validateAndSetRelations(existingEnquiry, requestDTO);
            Enquiry updatedEnquiry = repository.save(existingEnquiry);
            return mapToResponseDTO(updatedEnquiry);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Enquiry", ex);
        }
    }

    @Override
    public List<EnquiryResponseDTO> getAllEnquiriesOfUser(Integer userId) {
        List<Enquiry> enquiryList = repository.findByUserUserId(userId);
        List<EnquiryResponseDTO> dtoList = new ArrayList<>();
        for (Enquiry enquiry : enquiryList) {
            dtoList.add(mapToResponseDTO(enquiry));
        }
        return dtoList;
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Enquiry not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Enquiry with ID: " + id, ex);
        }
    }

    private void validateAndSetRelations(Enquiry enquiry, EnquiryRequestDTO requestDTO) {
        if (requestDTO.getUserId() != null) {
            Optional<UserMaster> userOpt = userMasterDao.findById(requestDTO.getUserId());
            if (userOpt.isPresent()) {
                enquiry.setUser(userOpt.get());
            } else {
                throw new ResourceNotFoundException("User not found with ID: " + requestDTO.getUserId());
            }
        }

        if (requestDTO.getBrandId() != null) {
            Optional<Brand> brandOpt = brandDao.findById(requestDTO.getBrandId());
            if (brandOpt.isPresent()) {
                enquiry.setBrand(brandOpt.get());
            } else {
                throw new ResourceNotFoundException("Brand not found with ID: " + requestDTO.getBrandId());
            }
        }

        if (requestDTO.getStatusId() != null) {
            Optional<Status> statusOpt = statusDao.findById(requestDTO.getStatusId());
            if (statusOpt.isPresent()) {
                if ("ENQUIRY".equalsIgnoreCase(statusOpt.get().getStatusType())) {
                    enquiry.setStatus(statusOpt.get());
                }
                else {
                    throw new ResourceNotFoundException("Status has to be of type enquiry: " + requestDTO.getStatusId());
                }
            } else {
                throw new ResourceNotFoundException("Status not found with ID: " + requestDTO.getStatusId());
            }
        } else {
            Optional<Status> defaultStatusOpt = statusDao.getStatusByNameAndType("Pending", "ENQUIRY");
            if (defaultStatusOpt.isEmpty()) {
                defaultStatusOpt = statusDao.getStatusByNameAndType("Open", "ENQUIRY");
            }
            if (defaultStatusOpt.isEmpty()) {
                List<Status> enquiryStatuses = statusDao.getStatusesByType("ENQUIRY");
                if (!enquiryStatuses.isEmpty()) {
                    defaultStatusOpt = Optional.of(enquiryStatuses.get(0));
                }
            }
            if (defaultStatusOpt.isPresent()) {
                enquiry.setStatus(defaultStatusOpt.get());
            } else {
                Status newStatus = new Status();
                newStatus.setStatusName("Pending");
                newStatus.setStatusType("ENQUIRY");
                newStatus.setStatusDescription("Pending Enquiry Status");
                newStatus.setStatusFlg(1);
                Status savedStatus = statusDao.save(newStatus);
                enquiry.setStatus(savedStatus);
            }
        }
    }

    private EnquiryResponseDTO mapToResponseDTO(Enquiry enquiry) {
        EnquiryResponseDTO dto = modelMapper.map(enquiry, EnquiryResponseDTO.class);
        if (enquiry.getUser() != null) {
            dto.setUserFirstName(enquiry.getUser().getFirstName());
            dto.setUserLastName(enquiry.getUser().getLastName());
        }
        if (enquiry.getBrand() != null) {
            dto.setBrandName(enquiry.getBrand().getBrandName());
        }
        if (enquiry.getStatus() != null) {
            dto.setStatusName(enquiry.getStatus().getStatusName());
        }
        return dto;
    }
}
