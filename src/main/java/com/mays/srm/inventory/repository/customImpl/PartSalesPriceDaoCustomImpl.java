package com.mays.srm.inventory.repository.customImpl;

import com.mays.srm.inventory.dto.resDTO.PartPriceCombinedResponseDTO;
import com.mays.srm.inventory.repository.custom.PartSalesPriceDaoCustom;
import com.mays.srm.inventory.util.InventoryQueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PartSalesPriceDaoCustomImpl implements PartSalesPriceDaoCustom {

    private static final String COMBINED_SQL = """
            SELECT
              pl.part_cat_id,
              pl.part_name,
              pl.sku,
              psp.sales_price_id,
              psp.sales_price,
              psp.effective_from AS sales_effective_from,
              psp.effective_to AS sales_effective_to,
              ppp.purchase_price_id,
              ppp.purchase_price,
              ppp.effective_from AS purchase_effective_from,
              ppp.effective_to AS purchase_effective_to,
              COALESCE(psp.currency, ppp.currency, 'INR') AS currency,
              COALESCE(psp.remarks, ppp.remarks) AS remarks,
              CASE
                WHEN psp.is_active = 0 OR ppp.is_active = 0 THEN 0
                WHEN psp.sales_price_id IS NULL AND ppp.purchase_price_id IS NULL THEN pl.is_active
                ELSE COALESCE(psp.is_active, ppp.is_active, 1)
              END AS is_active,
              COALESCE(psp.created_by, ppp.created_by) AS created_by,
              ec.employee_name AS created_by_name,
              COALESCE(psp.updated_by, ppp.updated_by) AS updated_by,
              eu.employee_name AS updated_by_name,
              COALESCE(psp.created_at, ppp.created_at, pl.created_at) AS created_at,
              COALESCE(psp.updated_at, ppp.updated_at, pl.updated_at) AS updated_at
            FROM product_list pl
            LEFT JOIN part_sales_price psp ON psp.part_cat_id = pl.part_cat_id
            LEFT JOIN part_purchase_price ppp ON ppp.part_cat_id = pl.part_cat_id
            LEFT JOIN Employee ec ON ec.employee_id = COALESCE(psp.created_by, ppp.created_by)
            LEFT JOIN Employee eu ON eu.employee_id = COALESCE(psp.updated_by, ppp.updated_by)
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<PartPriceCombinedResponseDTO> findAllCombined() {
        List<Object[]> rows = entityManager.createNativeQuery(
                        COMBINED_SQL + " ORDER BY pl.part_name")
                .getResultList();

        List<PartPriceCombinedResponseDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(mapRow(row));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<PartPriceCombinedResponseDTO> findCombinedByPartCatId(Integer partCatId) {
        List<Object[]> rows = entityManager.createNativeQuery(
                        COMBINED_SQL + " WHERE pl.part_cat_id = :partCatId")
                .setParameter("partCatId", partCatId)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapRow(rows.get(0)));
    }

    private PartPriceCombinedResponseDTO mapRow(Object[] row) {
        return new PartPriceCombinedResponseDTO(
                InventoryQueryMapper.toInteger(row[0]),
                InventoryQueryMapper.toStringValue(row[1]),
                InventoryQueryMapper.toStringValue(row[2]),
                InventoryQueryMapper.toInteger(row[3]),
                InventoryQueryMapper.toBigDecimal(row[4]),
                InventoryQueryMapper.toLocalDate(row[5]),
                InventoryQueryMapper.toLocalDate(row[6]),
                InventoryQueryMapper.toInteger(row[7]),
                InventoryQueryMapper.toBigDecimal(row[8]),
                InventoryQueryMapper.toLocalDate(row[9]),
                InventoryQueryMapper.toLocalDate(row[10]),
                InventoryQueryMapper.toStringValue(row[11]),
                InventoryQueryMapper.toStringValue(row[12]),
                InventoryQueryMapper.toBoolean(row[13]),
                InventoryQueryMapper.toInteger(row[14]),
                InventoryQueryMapper.toStringValue(row[15]),
                InventoryQueryMapper.toInteger(row[16]),
                InventoryQueryMapper.toStringValue(row[17]),
                InventoryQueryMapper.toLocalDateTime(row[18]),
                InventoryQueryMapper.toLocalDateTime(row[19]));
    }
}
