package com.mays.srm.inventory.dto.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryRequestDTO {
    private String productName;
    private Integer brandId;
    private String specification;
    private String descr;
    private BigDecimal sellingPrice;
    private BigDecimal buyingPrice;
    private Integer stock;
    private Integer branchId;
}
