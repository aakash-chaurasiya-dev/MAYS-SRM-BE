package com.mays.srm.inventory.dto.resDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryResponseDTO {
    private Integer productId;
    private String productName;
    private String sku;
    private Integer deviceTypeId;
    private String deviceTypeName;
    private String specification;
    private String descr;
    private BigDecimal sellingPrice;
    private BigDecimal buyingPrice;
    private Integer stock;
    private Integer minStock;
    private String hsnCode;
    private Integer branchId;
    private String branchName;
    private Boolean isActive;
    private LocalDateTime lastUpdationDate;
}
