package com.mays.srm.billing.controller;
import com.mays.srm.billing.dto.request.ChargeTypeRequestDTO;
import com.mays.srm.billing.dto.resDTO.ChargeTypeResponseDTO;
import com.mays.srm.billing.service.ChargeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charge-types")
public class ChargeTypeController {

    @Autowired
    private ChargeTypeService chargeTypeService;

    @PostMapping
    public ResponseEntity<ChargeTypeResponseDTO> createChargeType(@RequestBody ChargeTypeRequestDTO requestDTO) {
        ChargeTypeResponseDTO responseDTO = chargeTypeService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargeTypeResponseDTO> getChargeTypeById(@PathVariable Integer id) {
        ChargeTypeResponseDTO responseDTO = chargeTypeService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ChargeTypeResponseDTO>> getAllChargeTypes() {
        List<ChargeTypeResponseDTO> responseDTOs = chargeTypeService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargeTypeResponseDTO> updateChargeType(@PathVariable Integer id, @RequestBody ChargeTypeRequestDTO requestDTO) {
        ChargeTypeResponseDTO updatedDto = chargeTypeService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChargeType(@PathVariable Integer id) {
        chargeTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
