package com.mays.srm.inventory.service.impl;
import com.mays.srm.billing.entities.Billing;
import com.mays.srm.dao.core.BranchDao;
import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.InventoryDao;
import com.mays.srm.inventory.dto.request.InventoryRequestDTO;
import com.mays.srm.inventory.dto.resDTO.InventoryResponseDTO;
import com.mays.srm.organization.entities.Branch;
import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.inventory.entities.Inventory;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.service.InventoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryDao repository;
    private final BrandDao brandDao;
    private final BranchDao branchDao;
    private final ModelMapper modelMapper;

    @Autowired
    public InventoryServiceImpl(InventoryDao repository, BrandDao brandDao, BranchDao branchDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.brandDao = brandDao;
        this.branchDao = branchDao;
        this.modelMapper = modelMapper;
    }

    @Override
    public InventoryResponseDTO create(InventoryRequestDTO requestDTO) {
        try {
            Inventory inventory = modelMapper.map(requestDTO, Inventory.class);
            validateAndSetRelations(inventory, requestDTO);
            
            Inventory savedInventory = repository.save(inventory);
            return mapToResponseDTO(savedInventory);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Inventory record", ex);
        }
    }

    @Override
    public InventoryResponseDTO getById(Integer id) {
        Optional<Inventory> inventoryOpt = repository.findById(id);
        if (inventoryOpt.isPresent()) {
            return mapToResponseDTO(inventoryOpt.get());
        } else {
            throw new ResourceNotFoundException("Inventory record not found with ID: " + id);
        }
    }

    @Override
    public List<InventoryResponseDTO> getAll() {
        List<Inventory> inventoryList = repository.findAll();
        List<InventoryResponseDTO> dtoList = new ArrayList<>();
        for (Inventory inventory : inventoryList) {
            dtoList.add(mapToResponseDTO(inventory));
        }
        return dtoList;
    }

    @Override
    public InventoryResponseDTO update(Integer id, InventoryRequestDTO requestDTO) {
        Optional<Inventory> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Inventory record not found with ID: " + id);
        }
        
        Inventory existingInventory = existingOpt.get();
        modelMapper.map(requestDTO, existingInventory);
        
        existingInventory.setProductId(id); // Ensure ID is not changed

        try {
            validateAndSetRelations(existingInventory, requestDTO);
            Inventory updatedInventory = repository.save(existingInventory);
            return mapToResponseDTO(updatedInventory);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Inventory record", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Inventory record not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Inventory record because it is currently assigned to a Billing record.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Inventory record with ID: " + id, ex);
        }
    }

    private void validateAndSetRelations(Inventory inventory, InventoryRequestDTO requestDTO) {
        // Set Brand and derive DeviceType from it
        if (requestDTO.getBrandId() != null) {
            Optional<Brand> brandOpt = brandDao.findById(requestDTO.getBrandId());
            if (brandOpt.isPresent()) {
                Brand brand = brandOpt.get();
                inventory.setBrand(brand);
                // Automatically set the DeviceType from the Brand
                if (brand.getDeviceType() != null) {
                    inventory.setDeviceType(brand.getDeviceType());
                } else {
                    // This case might indicate a data integrity issue (a brand should have a device type)
                    inventory.setDeviceType(null);
                }
            } else {
                throw new ResourceNotFoundException("Brand not found with ID: " + requestDTO.getBrandId());
            }
        } else {
            inventory.setBrand(null);
            inventory.setDeviceType(null);
        }

        // Set Branch
        if (requestDTO.getBranchId() != null) {
            Optional<Branch> branchOpt = branchDao.findById(requestDTO.getBranchId());
            if (branchOpt.isPresent()) {
                inventory.setBranch(branchOpt.get());
            } else {
                throw new ResourceNotFoundException("Branch not found with ID: " + requestDTO.getBranchId());
            }
        } else {
            inventory.setBranch(null);
        }
    }

    private InventoryResponseDTO mapToResponseDTO(Inventory inventory) {
        InventoryResponseDTO dto = modelMapper.map(inventory, InventoryResponseDTO.class);
        
        if (inventory.getDeviceType() != null) {
            dto.setDeviceTypeName(inventory.getDeviceType().getDeviceTypeName());
        }
        if (inventory.getBrand() != null) {
            dto.setBrandName(inventory.getBrand().getBrandName());
        }
        if (inventory.getBranch() != null) {
            dto.setBranchName(inventory.getBranch().getBranchName());
        }
        return dto;
    }
}
