package com.mays.srm.controller;

import com.mays.srm.service.GenericService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * A simple base controller to handle standard CRUD operations.
 * If a Controller extends this, it automatically gets Create, Read, Update, Delete endpoints.
 */
public abstract class AbstractController<T, ID> {

    // Any controller that extends this must tell us which Service to use
    protected abstract GenericService<T, ID> getService();

    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity) {
        T createdEntity = getService().create(entity);
        return ResponseEntity.ok(createdEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable ID id) {
        Optional<T> entityOpt = getService().getById(id);
        
        if (entityOpt.isPresent()) {
             return ResponseEntity.ok(entityOpt.get());
        } else {
             // Fallback just in case a service doesn't throw the exception correctly
             return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<T>> getAll() {
        List<T> allEntities = getService().getAll();
        return ResponseEntity.ok(allEntities);
    }

    @PutMapping
    public ResponseEntity<T> update(@RequestBody T entity) {
        T updatedEntity = getService().update(entity);
        return ResponseEntity.ok(updatedEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        getService().delete(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content for a successful delete
    }
}
