package com.mays.srm.inventory.dto.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryRequestDTO {
    private String productName;
    private String sku;
    private Integer deviceTypeId;
    private String specification;
    private String descr;
    private BigDecimal sellingPrice;
    private BigDecimal buyingPrice;
    private Integer stock;
    private Integer minStock;
    private String hsnCode;
    private Integer branchId;
    private Boolean isActive;
}
