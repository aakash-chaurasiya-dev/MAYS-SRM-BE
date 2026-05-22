package com.mays.srm.controller;

import com.mays.srm.entity.ChargeType;
import com.mays.srm.service.ChargeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charge-types")
public class ChargeTypeController {

    @Autowired
    private ChargeTypeService chargeTypeService;

    @PostMapping
    public ResponseEntity<ChargeType> createChargeType(@RequestBody ChargeType chargeType) {
        ChargeType createdChargeType = chargeTypeService.createChargeType(chargeType);
        return new ResponseEntity<>(createdChargeType, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargeType> getChargeTypeById(@PathVariable Integer id) {
        ChargeType chargeType = chargeTypeService.getChargeTypeById(id);
        if (chargeType != null) {
            return new ResponseEntity<>(chargeType, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<ChargeType>> getAllChargeTypes() {
        List<ChargeType> chargeTypes = chargeTypeService.getAllChargeTypes();
        return new ResponseEntity<>(chargeTypes, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChargeType(@PathVariable Integer id) {
        chargeTypeService.deleteChargeType(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
