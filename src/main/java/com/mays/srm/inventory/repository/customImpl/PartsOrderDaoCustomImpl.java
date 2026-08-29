package com.mays.srm.inventory.repository.customImpl;

import com.mays.srm.inventory.dto.resDTO.PartsOrderModalResponseDTO;
import com.mays.srm.inventory.dto.resDTO.PartsOrderSummaryResponseDTO;
import com.mays.srm.inventory.repository.PartsMasterDao;
import com.mays.srm.inventory.repository.custom.PartsOrderDaoCustom;
import com.mays.srm.inventory.util.InventoryQueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PartsOrderDaoCustomImpl implements PartsOrderDaoCustom {

    private static final String MODAL_HEADER_SELECT = """
            SELECT
              po.order_id,
              po.ticket_id,
              po.part_cat_id,
              po.ticket_part_id,
              pl.part_name,
              dt.device_type_name,
              b.brand_name,
              pl.sku,
              po.quantity,
              po.status,
              po.total_price,
              po.currency,
              po.remarks,
              po.ordered_at,
              po.received_at
            FROM parts_order po
            JOIN product_list pl ON pl.part_cat_id = po.part_cat_id
            LEFT JOIN Device_Type dt ON dt.device_type_id = pl.device_type_id
            LEFT JOIN brand b ON b.brand_id = pl.brand_id
            """;

    private static final String SUMMARY_SQL = """
            SELECT
              po.order_id,
              po.ticket_id,
              po.part_cat_id,
              pl.part_name,
              po.quantity,
              po.status,
              po.total_price,
              po.ordered_by,
              eo.employee_name AS ordered_by_name,
              po.created_at
            FROM parts_order po
            JOIN product_list pl ON pl.part_cat_id = po.part_cat_id
            LEFT JOIN Employee eo ON eo.employee_id = po.ordered_by
            ORDER BY po.created_at DESC
            """;

    private final PartsMasterDao partsMasterDao;

    @PersistenceContext
    private EntityManager entityManager;

    public PartsOrderDaoCustomImpl(PartsMasterDao partsMasterDao) {
        this.partsMasterDao = partsMasterDao;
    }

    @Override
    public Optional<PartsOrderModalResponseDTO> findModalByTicketPartId(Integer ticketPartId) {
        return findModal(MODAL_HEADER_SELECT + " WHERE po.ticket_part_id = :ticketPartId LIMIT 1",
                "ticketPartId", ticketPartId);
    }

    @Override
    public Optional<PartsOrderModalResponseDTO> findModalByOrderId(Integer orderId) {
        return findModal(MODAL_HEADER_SELECT + " WHERE po.order_id = :orderId LIMIT 1",
                "orderId", orderId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PartsOrderSummaryResponseDTO> findAllSummaries() {
        List<Object[]> rows = entityManager.createNativeQuery(SUMMARY_SQL).getResultList();
        List<PartsOrderSummaryResponseDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new PartsOrderSummaryResponseDTO(
                    InventoryQueryMapper.toInteger(row[0]),
                    InventoryQueryMapper.toInteger(row[1]),
                    InventoryQueryMapper.toInteger(row[2]),
                    InventoryQueryMapper.toStringValue(row[3]),
                    InventoryQueryMapper.toInteger(row[4]),
                    InventoryQueryMapper.toStringValue(row[5]),
                    InventoryQueryMapper.toBigDecimal(row[6]),
                    InventoryQueryMapper.toInteger(row[7]),
                    InventoryQueryMapper.toStringValue(row[8]),
                    InventoryQueryMapper.toLocalDateTime(row[9])));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Optional<PartsOrderModalResponseDTO> findModal(String sql, String paramName, Integer paramValue) {
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter(paramName, paramValue)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = rows.get(0);
        PartsOrderModalResponseDTO modal = new PartsOrderModalResponseDTO(
                InventoryQueryMapper.toInteger(row[0]),
                InventoryQueryMapper.toInteger(row[1]),
                InventoryQueryMapper.toInteger(row[2]),
                InventoryQueryMapper.toInteger(row[3]),
                InventoryQueryMapper.toStringValue(row[4]),
                InventoryQueryMapper.toStringValue(row[5]),
                InventoryQueryMapper.toStringValue(row[6]),
                InventoryQueryMapper.toStringValue(row[7]),
                InventoryQueryMapper.toInteger(row[8]),
                InventoryQueryMapper.toStringValue(row[9]),
                InventoryQueryMapper.toBigDecimal(row[10]),
                InventoryQueryMapper.toStringValue(row[11]),
                InventoryQueryMapper.toStringValue(row[12]),
                InventoryQueryMapper.toLocalDateTime(row[13]),
                InventoryQueryMapper.toLocalDateTime(row[14]));

        if (modal.getOrderId() != null) {
            modal.setLines(partsMasterDao.findLinesByOrderId(modal.getOrderId()));
        }
        return Optional.of(modal);
    }
}
