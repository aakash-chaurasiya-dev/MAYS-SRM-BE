package com.mays.srm.inventory.repository.customImpl;

import com.mays.srm.inventory.dto.resDTO.QuoteResponseDTO;
import com.mays.srm.inventory.repository.custom.QuoteDaoCustom;
import com.mays.srm.inventory.util.InventoryQueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class QuoteDaoCustomImpl implements QuoteDaoCustom {

    private static final String BASE_SELECT = """
            SELECT
              q.quote_id,
              q.quote_no,
              q.ticket_id,
              q.part_cat_id,
              q.ticket_part_id,
              pl.part_name,
              q.sales_price,
              q.description,
              q.subject,
              q.body,
              q.status,
              q.valid_until,
              q.sent_at,
              es.employee_name AS sent_by_name,
              q.created_at,
              q.updated_at
            FROM quotes q
            JOIN product_list pl ON pl.part_cat_id = q.part_cat_id
            LEFT JOIN Employee es ON es.employee_id = q.sent_by
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<QuoteResponseDTO> findDetailsByTicketPartId(Integer ticketPartId) {
        return executeQuery(BASE_SELECT + " WHERE q.ticket_part_id = :ticketPartId ORDER BY q.created_at DESC",
                "ticketPartId", ticketPartId);
    }

    @Override
    public List<QuoteResponseDTO> findDetailsByTicketId(Integer ticketId) {
        return executeQuery(BASE_SELECT + " WHERE q.ticket_id = :ticketId ORDER BY q.created_at DESC",
                "ticketId", ticketId);
    }

    @SuppressWarnings("unchecked")
    private List<QuoteResponseDTO> executeQuery(String sql, String paramName, Integer paramValue) {
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter(paramName, paramValue)
                .getResultList();

        List<QuoteResponseDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(mapRow(row));
        }
        return result;
    }

    private QuoteResponseDTO mapRow(Object[] row) {
        return new QuoteResponseDTO(
                InventoryQueryMapper.toInteger(row[0]),
                InventoryQueryMapper.toStringValue(row[1]),
                InventoryQueryMapper.toInteger(row[2]),
                InventoryQueryMapper.toInteger(row[3]),
                InventoryQueryMapper.toInteger(row[4]),
                InventoryQueryMapper.toStringValue(row[5]),
                InventoryQueryMapper.toBigDecimal(row[6]),
                InventoryQueryMapper.toStringValue(row[7]),
                InventoryQueryMapper.toStringValue(row[8]),
                InventoryQueryMapper.toStringValue(row[9]),
                InventoryQueryMapper.toStringValue(row[10]),
                InventoryQueryMapper.toLocalDate(row[11]),
                InventoryQueryMapper.toLocalDateTime(row[12]),
                InventoryQueryMapper.toStringValue(row[13]),
                InventoryQueryMapper.toLocalDateTime(row[14]),
                InventoryQueryMapper.toLocalDateTime(row[15]));
    }
}
