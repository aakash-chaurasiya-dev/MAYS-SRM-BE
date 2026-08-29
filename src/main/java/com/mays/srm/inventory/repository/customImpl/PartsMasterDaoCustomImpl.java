package com.mays.srm.inventory.repository.customImpl;

import com.mays.srm.inventory.dto.resDTO.PartsMasterLineResponseDTO;
import com.mays.srm.inventory.repository.custom.PartsMasterDaoCustom;
import com.mays.srm.inventory.util.InventoryQueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PartsMasterDaoCustomImpl implements PartsMasterDaoCustom {

    private static final String FIND_LINES_SQL = """
            SELECT
              pm.individual_part_id,
              pm.order_id,
              pm.part_sr_no,
              pm.barcode,
              pm.source,
              pm.damaged_flag,
              pm.returned_flag,
              pm.vendor_damage_return,
              vdpr.return_part_sr_no,
              pm.replaced_id,
              rpm.part_sr_no AS replaced_part_sr_no,
              pm.received,
              pm.received_at,
              pp.sales_price,
              pp.purchase_price,
              pm.is_active
            FROM parts_master pm
            LEFT JOIN vendor_damage_part_return vdpr ON vdpr.individual_part_id = pm.individual_part_id
            LEFT JOIN parts_master rpm ON rpm.individual_part_id = pm.replaced_id
            LEFT JOIN part_price pp ON pp.individual_part_id = pm.individual_part_id
            WHERE pm.order_id = :orderId
              AND pm.is_active = 1
            ORDER BY pm.individual_part_id
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<PartsMasterLineResponseDTO> findLinesByOrderId(Integer orderId) {
        List<Object[]> rows = entityManager.createNativeQuery(FIND_LINES_SQL)
                .setParameter("orderId", orderId)
                .getResultList();

        List<PartsMasterLineResponseDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(mapRow(row));
        }
        return result;
    }

    private PartsMasterLineResponseDTO mapRow(Object[] row) {
        return new PartsMasterLineResponseDTO(
                InventoryQueryMapper.toInteger(row[0]),
                InventoryQueryMapper.toInteger(row[1]),
                InventoryQueryMapper.toStringValue(row[2]),
                InventoryQueryMapper.toStringValue(row[3]),
                InventoryQueryMapper.toStringValue(row[4]),
                InventoryQueryMapper.toBoolean(row[5]),
                InventoryQueryMapper.toBoolean(row[6]),
                InventoryQueryMapper.toBoolean(row[7]),
                InventoryQueryMapper.toStringValue(row[8]),
                InventoryQueryMapper.toInteger(row[9]),
                InventoryQueryMapper.toStringValue(row[10]),
                InventoryQueryMapper.toBoolean(row[11]),
                InventoryQueryMapper.toLocalDateTime(row[12]),
                InventoryQueryMapper.toBigDecimal(row[13]),
                InventoryQueryMapper.toBigDecimal(row[14]),
                InventoryQueryMapper.toBoolean(row[15]));
    }
}
