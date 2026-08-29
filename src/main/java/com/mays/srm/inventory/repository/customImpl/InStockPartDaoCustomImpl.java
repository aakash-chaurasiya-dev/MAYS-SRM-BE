package com.mays.srm.inventory.repository.customImpl;

import com.mays.srm.inventory.dto.resDTO.InStockPartOptionDTO;
import com.mays.srm.inventory.dto.resDTO.InStockPartResponseDTO;
import com.mays.srm.inventory.repository.custom.InStockPartDaoCustom;
import com.mays.srm.inventory.util.InventoryQueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InStockPartDaoCustomImpl implements InStockPartDaoCustom {

    private static final String AVAILABLE_SQL = """
            SELECT
              isp.individual_part_id,
              isp.part_cat_id,
              isp.part_sr_no,
              isp.barcode,
              pp.sales_price,
              pp.purchase_price
            FROM in_stock_part isp
            LEFT JOIN part_price pp ON pp.individual_part_id = isp.individual_part_id
            WHERE isp.part_cat_id = :partCatId
              AND isp.received = 0
              AND isp.is_active = 1
            ORDER BY isp.part_sr_no
            """;

    private static final String DETAILS_SQL = """
            SELECT
              isp.individual_part_id,
              isp.part_cat_id,
              pl.part_name,
              pl.sku,
              isp.part_sr_no,
              isp.barcode,
              isp.source,
              isp.received,
              isp.received_at,
              isp.remarks,
              isp.is_active,
              pp.part_price_id,
              pp.sales_price,
              pp.purchase_price,
              pp.currency,
              isp.created_by,
              ec.employee_name AS created_by_name,
              isp.updated_by,
              eu.employee_name AS updated_by_name,
              isp.created_at,
              isp.updated_at
            FROM in_stock_part isp
            JOIN product_list pl ON pl.part_cat_id = isp.part_cat_id
            LEFT JOIN part_price pp ON pp.individual_part_id = isp.individual_part_id
            LEFT JOIN Employee ec ON ec.employee_id = isp.created_by
            LEFT JOIN Employee eu ON eu.employee_id = isp.updated_by
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<InStockPartOptionDTO> findAvailableOptions(Integer partCatId) {
        List<Object[]> rows = entityManager.createNativeQuery(AVAILABLE_SQL)
                .setParameter("partCatId", partCatId)
                .getResultList();

        List<InStockPartOptionDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new InStockPartOptionDTO(
                    InventoryQueryMapper.toInteger(row[0]),
                    InventoryQueryMapper.toInteger(row[1]),
                    InventoryQueryMapper.toStringValue(row[2]),
                    InventoryQueryMapper.toStringValue(row[3]),
                    InventoryQueryMapper.toBigDecimal(row[4]),
                    InventoryQueryMapper.toBigDecimal(row[5])));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<InStockPartResponseDTO> findAllDetails(Integer partCatId) {
        StringBuilder sql = new StringBuilder(DETAILS_SQL);
        if (partCatId != null) {
            sql.append(" WHERE isp.part_cat_id = :partCatId");
        }
        sql.append(" ORDER BY isp.created_at DESC");

        Query query = entityManager.createNativeQuery(sql.toString());
        if (partCatId != null) {
            query.setParameter("partCatId", partCatId);
        }

        List<Object[]> rows = query.getResultList();
        List<InStockPartResponseDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(mapDetails(row));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<InStockPartResponseDTO> findDetailsById(Integer individualPartId) {
        List<Object[]> rows = entityManager.createNativeQuery(
                        DETAILS_SQL + " WHERE isp.individual_part_id = :id")
                .setParameter("id", individualPartId)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapDetails(rows.get(0)));
    }

    private InStockPartResponseDTO mapDetails(Object[] row) {
        return new InStockPartResponseDTO(
                InventoryQueryMapper.toInteger(row[0]),
                InventoryQueryMapper.toInteger(row[1]),
                InventoryQueryMapper.toStringValue(row[2]),
                InventoryQueryMapper.toStringValue(row[3]),
                InventoryQueryMapper.toStringValue(row[4]),
                InventoryQueryMapper.toStringValue(row[5]),
                InventoryQueryMapper.toStringValue(row[6]),
                InventoryQueryMapper.toBoolean(row[7]),
                InventoryQueryMapper.toLocalDateTime(row[8]),
                InventoryQueryMapper.toStringValue(row[9]),
                InventoryQueryMapper.toBoolean(row[10]),
                InventoryQueryMapper.toInteger(row[11]),
                InventoryQueryMapper.toBigDecimal(row[12]),
                InventoryQueryMapper.toBigDecimal(row[13]),
                InventoryQueryMapper.toStringValue(row[14]),
                InventoryQueryMapper.toInteger(row[15]),
                InventoryQueryMapper.toStringValue(row[16]),
                InventoryQueryMapper.toInteger(row[17]),
                InventoryQueryMapper.toStringValue(row[18]),
                InventoryQueryMapper.toLocalDateTime(row[19]),
                InventoryQueryMapper.toLocalDateTime(row[20]));
    }
}
