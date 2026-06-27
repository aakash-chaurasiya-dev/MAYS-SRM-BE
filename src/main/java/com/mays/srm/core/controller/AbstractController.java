package com.mays.srm.core.controller;
import com.mays.srm.service.GenericService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A simple base controller to handle standard CRUD operations using DTOs.
 * If a Controller extends this, it automatically gets Create, Read, Update, Delete endpoints.
 * @param <REQ_DTO> The Request DTO for creating/updating.
 * @param <RES_DTO> The Response DTO for returning data.
 * @param <ID> The type of the entity's ID.
 */
public abstract class AbstractController<REQ_DTO, RES_DTO, ID> {

    // Any controller that extends this must tell us which Service to use
    protected abstract GenericService<REQ_DTO, RES_DTO, ID> getService();

    @PostMapping
    public ResponseEntity<RES_DTO> create(@RequestBody REQ_DTO requestDTO) {
        RES_DTO createdDto = getService().create(requestDTO);
        return ResponseEntity.ok(createdDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RES_DTO> getById(@PathVariable ID id) {
        RES_DTO responseDto = getService().getById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<RES_DTO>> getAll() {
        List<RES_DTO> dtoList = getService().getAll();
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RES_DTO> update(@PathVariable ID id, @RequestBody REQ_DTO requestDTO) {
        RES_DTO updatedDto = getService().update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        getService().delete(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content for a successful delete
    }
}
