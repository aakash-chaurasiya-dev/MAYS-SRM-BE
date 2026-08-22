package com.mays.srm.inventory.controller;

import com.mays.srm.inventory.dto.request.ProductListRequestDTO;
import com.mays.srm.inventory.dto.resDTO.ProductListOptionDTO;
import com.mays.srm.inventory.dto.resDTO.ProductListResponseDTO;
import com.mays.srm.inventory.service.ProductListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/products")
public class ProductListController {

    private final ProductListService productListService;

    public ProductListController(ProductListService productListService) {
        this.productListService = productListService;
    }

    @GetMapping
    public ResponseEntity<List<ProductListResponseDTO>> getAll() {
        return ResponseEntity.ok(productListService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductListOptionDTO>> search(
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(productListService.search(term, limit));
    }

    @GetMapping("/{partCatId}")
    public ResponseEntity<ProductListResponseDTO> getById(@PathVariable Integer partCatId) {
        return ResponseEntity.ok(productListService.getById(partCatId));
    }

    @PostMapping
    public ResponseEntity<ProductListResponseDTO> create(@RequestBody ProductListRequestDTO request) {
        return new ResponseEntity<>(productListService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{partCatId}")
    public ResponseEntity<ProductListResponseDTO> update(
            @PathVariable Integer partCatId,
            @RequestBody ProductListRequestDTO request) {
        return ResponseEntity.ok(productListService.update(partCatId, request));
    }

    @DeleteMapping("/{partCatId}")
    public ResponseEntity<Void> delete(@PathVariable Integer partCatId) {
        productListService.delete(partCatId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBulk(@RequestBody List<Integer> ids) {
        productListService.deleteBulk(ids);
        return ResponseEntity.noContent().build();
    }
}
