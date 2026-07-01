package com.mays.srm.inventory.dto.resDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryResponseDTO {
    private Integer productId;
    private String productName;
    private String deviceTypeName;
    private String brandName;
    private String specification;
    private String descr;
    private BigDecimal sellingPrice;
    private BigDecimal buyingPrice;
    private Integer stock;
    private String branchName;
    private LocalDateTime lastUpdationDate; // Added
}
