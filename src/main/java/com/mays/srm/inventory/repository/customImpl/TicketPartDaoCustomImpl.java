package com.mays.srm.inventory.repository.customImpl;

import com.mays.srm.inventory.dto.resDTO.TicketPartResponseDTO;
import com.mays.srm.inventory.repository.custom.TicketPartDaoCustom;
import com.mays.srm.inventory.util.InventoryQueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketPartDaoCustomImpl implements TicketPartDaoCustom {

    private static final String DETAILS_SELECT = """
            SELECT
              tp.ticket_part_id,
              tp.ticket_id,
              tp.part_cat_id,
              pl.part_name,
              pl.sku,
              dt.device_type_name,
              b.brand_name,
              tp.quantity,
              tp.remark,
              tp.manager_approval,
              tp.manager_approved_at,
              tp.part_status,
              tp.send_quotes,
              tp.quotes_sent_at,
              tp.customer_approval,
              tp.customer_approved_at,
              q.quote_id,
              q.status AS quote_status,
              po.order_id,
              po.status AS order_status,
              CASE WHEN tp.manager_approval = 1
                   AND tp.part_status IN ('APPROVED', 'QUOTED', 'ORDERED', 'PARTIAL', 'RECEIVED')
                   THEN 1 ELSE 0 END AS can_quote,
              CASE WHEN tp.manager_approval = 1
                   AND tp.customer_approval = 1
                   AND tp.part_status IN ('APPROVED', 'QUOTED', 'ORDERED', 'PARTIAL', 'RECEIVED')
                   THEN 1 ELSE 0 END AS can_order,
              tp.created_by,
              ec.employee_name AS created_by_name,
              tp.updated_by,
              eu.employee_name AS updated_by_name,
              tp.created_at,
              tp.updated_at
            FROM ticket_parts tp
            JOIN product_list pl ON pl.part_cat_id = tp.part_cat_id
            LEFT JOIN Device_Type dt ON dt.device_type_id = pl.device_type_id
            LEFT JOIN brand b ON b.brand_id = pl.brand_id
            LEFT JOIN parts_order po ON po.ticket_part_id = tp.ticket_part_id
            LEFT JOIN Employee ec ON ec.employee_id = tp.created_by
            LEFT JOIN Employee eu ON eu.employee_id = tp.updated_by
            LEFT JOIN (
              SELECT q1.ticket_part_id, q1.quote_id, q1.status
              FROM quotes q1
              INNER JOIN (
                SELECT ticket_part_id, MAX(quote_id) AS max_quote_id
                FROM quotes
                GROUP BY ticket_part_id
              ) latest ON q1.ticket_part_id = latest.ticket_part_id
                AND q1.quote_id = latest.max_quote_id
            ) q ON q.ticket_part_id = tp.ticket_part_id
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<TicketPartResponseDTO> findDetailsByTicketId(Integer ticketId) {
        List<Object[]> rows = entityManager.createNativeQuery(
                        DETAILS_SELECT + " WHERE tp.ticket_id = :ticketId ORDER BY tp.created_at DESC")
                .setParameter("ticketId", ticketId)
                .getResultList();

        return mapRows(rows);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TicketPartResponseDTO> findAllDetails() {
        List<Object[]> rows = entityManager.createNativeQuery(
                        DETAILS_SELECT + " ORDER BY tp.created_at DESC")
                .getResultList();

        return mapRows(rows);
    }

    private List<TicketPartResponseDTO> mapRows(List<Object[]> rows) {
        List<TicketPartResponseDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(mapRow(row));
        }
        return result;
    }

    private TicketPartResponseDTO mapRow(Object[] row) {
        return new TicketPartResponseDTO(
                InventoryQueryMapper.toInteger(row[0]),
                InventoryQueryMapper.toInteger(row[1]),
                InventoryQueryMapper.toInteger(row[2]),
                InventoryQueryMapper.toStringValue(row[3]),
                InventoryQueryMapper.toStringValue(row[4]),
                InventoryQueryMapper.toStringValue(row[5]),
                InventoryQueryMapper.toStringValue(row[6]),
                InventoryQueryMapper.toInteger(row[7]),
                InventoryQueryMapper.toStringValue(row[8]),
                InventoryQueryMapper.toBoolean(row[9]),
                InventoryQueryMapper.toLocalDateTime(row[10]),
                InventoryQueryMapper.toStringValue(row[11]),
                InventoryQueryMapper.toBoolean(row[12]),
                InventoryQueryMapper.toLocalDateTime(row[13]),
                InventoryQueryMapper.toBoolean(row[14]),
                InventoryQueryMapper.toLocalDateTime(row[15]),
                InventoryQueryMapper.toInteger(row[16]),
                InventoryQueryMapper.toStringValue(row[17]),
                InventoryQueryMapper.toInteger(row[18]),
                InventoryQueryMapper.toStringValue(row[19]),
                InventoryQueryMapper.toBoolean(row[20]),
                InventoryQueryMapper.toBoolean(row[21]),
                InventoryQueryMapper.toInteger(row[22]),
                InventoryQueryMapper.toStringValue(row[23]),
                InventoryQueryMapper.toInteger(row[24]),
                InventoryQueryMapper.toStringValue(row[25]),
                InventoryQueryMapper.toLocalDateTime(row[26]),
                InventoryQueryMapper.toLocalDateTime(row[27]));
    }
}
