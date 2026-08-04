package com.mays.srm.inventory.service.impl;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.device.repository.DeviceTypeDao;
import com.mays.srm.organization.repository.BranchDao;
import com.mays.srm.inventory.repository.InventoryDao;
import com.mays.srm.inventory.dto.request.InventoryRequestDTO;
import com.mays.srm.inventory.dto.resDTO.InventoryResponseDTO;
import com.mays.srm.organization.entities.Branch;
import com.mays.srm.inventory.entities.Inventory;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.service.InventoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryDao repository;
    private final BranchDao branchDao;
    private final DeviceTypeDao deviceTypeDao;
    private final ModelMapper modelMapper;

    @Autowired
    public InventoryServiceImpl(
            InventoryDao repository,
            BranchDao branchDao,
            DeviceTypeDao deviceTypeDao,
            ModelMapper modelMapper
    ) {
        this.repository = repository;
        this.branchDao = branchDao;
        this.deviceTypeDao = deviceTypeDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @CacheEvict(value = "inventory", allEntries = true)
    public InventoryResponseDTO create(InventoryRequestDTO requestDTO) {
        try {
            Inventory inventory = modelMapper.map(requestDTO, Inventory.class);
            if (inventory.getStock() == null) {
                inventory.setStock(0);
            }
            if (inventory.getIsActive() == null) {
                inventory.setIsActive(true);
            }
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
    @Cacheable(value = "inventory")
    public List<InventoryResponseDTO> getAll() {
        List<Inventory> inventoryList = repository.findAll();
        List<InventoryResponseDTO> dtoList = new ArrayList<>();
        for (Inventory inventory : inventoryList) {
            dtoList.add(mapToResponseDTO(inventory));
        }
        return dtoList;
    }

    @Override
    @CacheEvict(value = "inventory", allEntries = true)
    public InventoryResponseDTO update(Integer id, InventoryRequestDTO requestDTO) {
        Optional<Inventory> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Inventory record not found with ID: " + id);
        }
        
        Inventory existingInventory = existingOpt.get();
        modelMapper.map(requestDTO, existingInventory);
        
        existingInventory.setProductId(id);

        try {
            validateAndSetRelations(existingInventory, requestDTO);
            if (existingInventory.getIsActive() == null) {
                existingInventory.setIsActive(true);
            }
            Inventory updatedInventory = repository.save(existingInventory);
            return mapToResponseDTO(updatedInventory);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Inventory record", ex);
        }
    }

    @Override
    @CacheEvict(value = "inventory", allEntries = true)
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
        if (requestDTO.getDeviceTypeId() != null) {
            DeviceType deviceType = deviceTypeDao.findById(requestDTO.getDeviceTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Device type not found with ID: " + requestDTO.getDeviceTypeId()));
            inventory.setDeviceType(deviceType);
        } else {
            inventory.setDeviceType(null);
        }

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
            dto.setDeviceTypeId(inventory.getDeviceType().getDeviceTypeId());
            dto.setDeviceTypeName(inventory.getDeviceType().getDeviceTypeName());
        }
        if (inventory.getBranch() != null) {
            dto.setBranchId(inventory.getBranch().getBranchId());
            dto.setBranchName(inventory.getBranch().getBranchName());
        }
        return dto;
    }
}
