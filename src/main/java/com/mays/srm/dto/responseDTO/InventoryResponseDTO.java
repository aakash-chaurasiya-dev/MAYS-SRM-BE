package com.mays.srm.dto.responseDTO;

import lombok.Data;
import java.math.BigDecimal;

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
}
