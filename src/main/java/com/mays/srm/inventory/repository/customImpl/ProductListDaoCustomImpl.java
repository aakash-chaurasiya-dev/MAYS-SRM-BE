package com.mays.srm.inventory.repository.customImpl;

import com.mays.srm.inventory.dto.resDTO.ProductListOptionDTO;
import com.mays.srm.inventory.dto.resDTO.ProductListResponseDTO;
import com.mays.srm.inventory.repository.custom.ProductListDaoCustom;
import com.mays.srm.inventory.util.InventoryQueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductListDaoCustomImpl implements ProductListDaoCustom {

    private static final String SEARCH_SQL = """
            SELECT
              pl.part_cat_id,
              pl.part_name,
              pl.sku,
              dt.device_type_name,
              b.brand_name,
              psp.sales_price,
              ppp.purchase_price
            FROM product_list pl
            LEFT JOIN Device_Type dt ON dt.device_type_id = pl.device_type_id
            LEFT JOIN brand b ON b.brand_id = pl.brand_id
            LEFT JOIN part_sales_price psp ON psp.part_cat_id = pl.part_cat_id AND psp.is_active = 1
            LEFT JOIN part_purchase_price ppp ON ppp.part_cat_id = pl.part_cat_id AND ppp.is_active = 1
            WHERE pl.is_active = 1
              AND (:term IS NULL OR :term = ''
                   OR pl.part_name LIKE CONCAT('%', :term, '%')
                   OR pl.sku LIKE CONCAT('%', :term, '%'))
            ORDER BY pl.part_name
            LIMIT :limit
            """;

    private static final String DETAILS_SQL = """
            SELECT
              pl.part_cat_id,
              pl.device_type_id,
              dt.device_type_name,
              pl.brand_id,
              b.brand_name,
              pl.part_name,
              pl.sku,
              pl.hsn_code,
              pl.specification,
              pl.descr,
              pl.is_active,
              s.stocks,
              s.min_stock,
              s.max_stock,
              pl.created_by,
              ec.employee_name AS created_by_name,
              pl.updated_by,
              eu.employee_name AS updated_by_name,
              pl.created_at,
              pl.updated_at
            FROM product_list pl
            LEFT JOIN Device_Type dt ON dt.device_type_id = pl.device_type_id
            LEFT JOIN brand b ON b.brand_id = pl.brand_id
            LEFT JOIN stocks s ON s.part_cat_id = pl.part_cat_id
            LEFT JOIN Employee ec ON ec.employee_id = pl.created_by
            LEFT JOIN Employee eu ON eu.employee_id = pl.updated_by
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<ProductListOptionDTO> searchOptions(String term, int limit) {
        String searchTerm = term != null ? term.trim() : "";

        List<Object[]> rows = entityManager.createNativeQuery(SEARCH_SQL)
                .setParameter("term", searchTerm)
                .setParameter("limit", limit)
                .getResultList();

        List<ProductListOptionDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new ProductListOptionDTO(
                    InventoryQueryMapper.toInteger(row[0]),
                    InventoryQueryMapper.toStringValue(row[1]),
                    InventoryQueryMapper.toStringValue(row[2]),
                    InventoryQueryMapper.toStringValue(row[3]),
                    InventoryQueryMapper.toStringValue(row[4]),
                    InventoryQueryMapper.toBigDecimal(row[5]),
                    InventoryQueryMapper.toBigDecimal(row[6])));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProductListResponseDTO> findAllDetails() {
        List<Object[]> rows = entityManager.createNativeQuery(
                        DETAILS_SQL + " ORDER BY pl.part_name")
                .getResultList();

        List<ProductListResponseDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(mapDetails(row));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ProductListResponseDTO> findDetailsById(Integer partCatId) {
        List<Object[]> rows = entityManager.createNativeQuery(
                        DETAILS_SQL + " WHERE pl.part_cat_id = :partCatId")
                .setParameter("partCatId", partCatId)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapDetails(rows.get(0)));
    }

    private ProductListResponseDTO mapDetails(Object[] row) {
        return new ProductListResponseDTO(
                InventoryQueryMapper.toInteger(row[0]),
                InventoryQueryMapper.toInteger(row[1]),
                InventoryQueryMapper.toStringValue(row[2]),
                InventoryQueryMapper.toInteger(row[3]),
                InventoryQueryMapper.toStringValue(row[4]),
                InventoryQueryMapper.toStringValue(row[5]),
                InventoryQueryMapper.toStringValue(row[6]),
                InventoryQueryMapper.toStringValue(row[7]),
                InventoryQueryMapper.toStringValue(row[8]),
                InventoryQueryMapper.toStringValue(row[9]),
                InventoryQueryMapper.toBoolean(row[10]),
                InventoryQueryMapper.toInteger(row[11]),
                InventoryQueryMapper.toInteger(row[12]),
                InventoryQueryMapper.toInteger(row[13]),
                InventoryQueryMapper.toInteger(row[14]),
                InventoryQueryMapper.toStringValue(row[15]),
                InventoryQueryMapper.toInteger(row[16]),
                InventoryQueryMapper.toStringValue(row[17]),
                InventoryQueryMapper.toLocalDateTime(row[18]),
                InventoryQueryMapper.toLocalDateTime(row[19]));
    }
}
