-- ============================================================
-- MAYS Inventory rebuild v2 — MySQL
-- created_by / updated_by / ordered_by / received_by / sent_by
--   → Employee(employee_id)
-- Backup first. FK targets: Ticket, Device_Type, brand, Employee
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

UPDATE billing SET product_id = NULL WHERE product_id IS NOT NULL;

DROP TABLE IF EXISTS parts_master_price;
DROP TABLE IF EXISTS part_price;
DROP TABLE IF EXISTS vendor_damage_part_return;
DROP TABLE IF EXISTS parts_master;
DROP TABLE IF EXISTS parts_order;
DROP TABLE IF EXISTS quotes;
DROP TABLE IF EXISTS ticket_parts;
DROP TABLE IF EXISTS in_stock_part;
DROP TABLE IF EXISTS stocks;
DROP TABLE IF EXISTS part_purchase_price;
DROP TABLE IF EXISTS part_sales_price;
DROP TABLE IF EXISTS product_list;
DROP TABLE IF EXISTS inventory_category;
DROP TABLE IF EXISTS inventory_log;
DROP TABLE IF EXISTS Parts;
DROP TABLE IF EXISTS Inventory;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------- Catalog ----------
CREATE TABLE product_list (
  part_cat_id    INT AUTO_INCREMENT PRIMARY KEY,
  device_type_id INT NULL,
  brand_id       INT NULL,
  part_name      VARCHAR(255) NOT NULL,
  sku            VARCHAR(100) NULL,
  hsn_code       VARCHAR(50) NULL,
  specification  TEXT NULL,
  descr          TEXT NULL,
  is_active      TINYINT(1) NOT NULL DEFAULT 1,
  created_by     INT NULL,
  updated_by     INT NULL,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_pl_device FOREIGN KEY (device_type_id) REFERENCES Device_Type(device_type_id),
  CONSTRAINT fk_pl_brand  FOREIGN KEY (brand_id) REFERENCES brand(brand_id),
  CONSTRAINT fk_pl_cby    FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_pl_uby    FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  UNIQUE KEY uk_pl_sku (sku),
  KEY idx_pl_part_name (part_name)
);

CREATE TABLE part_sales_price (
  sales_price_id INT AUTO_INCREMENT PRIMARY KEY,
  part_cat_id    INT NOT NULL,
  sales_price    DECIMAL(19,2) NOT NULL,
  currency       VARCHAR(10) NOT NULL DEFAULT 'INR',
  effective_from DATE NULL,
  effective_to   DATE NULL,
  remarks        TEXT NULL,
  is_active      TINYINT(1) NOT NULL DEFAULT 1,
  created_by     INT NULL,
  updated_by     INT NULL,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_psp_product FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_psp_cby     FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_psp_uby     FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  UNIQUE KEY uk_psp_part (part_cat_id)
);

CREATE TABLE part_purchase_price (
  purchase_price_id INT AUTO_INCREMENT PRIMARY KEY,
  part_cat_id       INT NOT NULL,
  purchase_price    DECIMAL(19,2) NOT NULL,
  currency          VARCHAR(10) NOT NULL DEFAULT 'INR',
  effective_from    DATE NULL,
  effective_to      DATE NULL,
  remarks           TEXT NULL,
  is_active         TINYINT(1) NOT NULL DEFAULT 1,
  created_by        INT NULL,
  updated_by        INT NULL,
  created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_ppp_product FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_ppp_cby     FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_ppp_uby     FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  UNIQUE KEY uk_ppp_part (part_cat_id)
);

CREATE TABLE stocks (
  stock_id     INT AUTO_INCREMENT PRIMARY KEY,
  part_cat_id  INT NOT NULL,
  stocks       INT NOT NULL DEFAULT 0,
  min_stock    INT NULL,
  max_stock    INT NULL,
  remarks      TEXT NULL,
  created_by   INT NULL,
  updated_by   INT NULL,
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_stocks_product FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_stocks_cby     FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_stocks_uby     FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  UNIQUE KEY uk_stocks_part (part_cat_id)
);

-- ---------- Serial / warehouse ----------
CREATE TABLE in_stock_part (
  individual_part_id INT AUTO_INCREMENT PRIMARY KEY,
  part_cat_id        INT NOT NULL,
  part_sr_no         VARCHAR(150) NULL,
  barcode            VARCHAR(150) NULL,
  source             VARCHAR(20) NOT NULL DEFAULT 'MARKET', -- MARKET | VENDOR
  received           TINYINT(1) NOT NULL DEFAULT 0,
  received_at        DATETIME NULL,
  ordered_by         INT NULL,
  remarks            TEXT NULL,
  is_active          TINYINT(1) NOT NULL DEFAULT 1,
  created_by         INT NULL,
  updated_by         INT NULL,
  created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_isp_product FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_isp_ordered FOREIGN KEY (ordered_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_isp_cby     FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_isp_uby     FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  KEY idx_isp_part_cat (part_cat_id),
  KEY idx_isp_sr (part_sr_no),
  KEY idx_isp_barcode (barcode)
);

CREATE TABLE part_price (
  part_price_id      INT AUTO_INCREMENT PRIMARY KEY,
  individual_part_id INT NOT NULL,
  part_cat_id        INT NOT NULL,
  sales_price        DECIMAL(19,2) NULL,
  purchase_price     DECIMAL(19,2) NULL,
  currency           VARCHAR(10) NOT NULL DEFAULT 'INR',
  remarks            TEXT NULL,
  created_by         INT NULL,
  updated_by         INT NULL,
  created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_pp_product FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_pp_cby     FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_pp_uby     FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  UNIQUE KEY uk_pp_individual (individual_part_id),
  KEY idx_pp_part_cat (part_cat_id)
);

-- ---------- Ticket flow ----------
CREATE TABLE ticket_parts (
  ticket_part_id       INT AUTO_INCREMENT PRIMARY KEY,
  ticket_id            INT NOT NULL,
  part_cat_id          INT NOT NULL,
  quantity             INT NOT NULL DEFAULT 1,
  remark               TEXT NULL,
  manager_approval     TINYINT(1) NULL,
  manager_approved_at  DATETIME NULL,
  part_status          VARCHAR(50) NOT NULL DEFAULT 'REQUESTED',
  -- REQUESTED | APPROVED | QUOTED | ORDERED | RECEIVED | REJECTED | CANCELLED
  send_quotes          TINYINT(1) NOT NULL DEFAULT 0,
  quotes_sent_at       DATETIME NULL,
  customer_approval    TINYINT(1) NULL,
  customer_approved_at DATETIME NULL,
  created_by           INT NULL,
  updated_by           INT NULL,
  created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_tp_ticket  FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
  CONSTRAINT fk_tp_product FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_tp_cby     FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_tp_uby     FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  KEY idx_tp_ticket (ticket_id),
  KEY idx_tp_status (part_status)
);

CREATE TABLE quotes (
  quote_id       INT AUTO_INCREMENT PRIMARY KEY,
  quote_no       VARCHAR(50) NULL,
  ticket_id      INT NOT NULL,
  part_cat_id    INT NOT NULL,
  ticket_part_id INT NULL,
  sales_price    DECIMAL(19,2) NULL,
  currency       VARCHAR(10) NOT NULL DEFAULT 'INR',
  description    TEXT NULL,
  subject        VARCHAR(255) NULL,
  body           TEXT NULL,
  status         VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
  -- DRAFT | SENT | ACCEPTED | REJECTED | EXPIRED
  valid_until    DATE NULL,
  sent_at        DATETIME NULL,
  sent_by        INT NULL,
  created_by     INT NULL,
  updated_by     INT NULL,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_q_ticket  FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
  CONSTRAINT fk_q_product FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_q_tp      FOREIGN KEY (ticket_part_id) REFERENCES ticket_parts(ticket_part_id),
  CONSTRAINT fk_q_sent    FOREIGN KEY (sent_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_q_cby     FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_q_uby     FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  UNIQUE KEY uk_quote_no (quote_no),
  KEY idx_q_ticket (ticket_id)
);

CREATE TABLE parts_order (
  order_id       INT AUTO_INCREMENT PRIMARY KEY,
  ticket_id      INT NOT NULL,
  part_cat_id    INT NOT NULL,
  ticket_part_id INT NULL,
  quantity       INT NOT NULL DEFAULT 1,
  status         VARCHAR(50) NOT NULL DEFAULT 'ORDERED',
  -- ORDERED | PARTIAL | RECEIVED | CANCELLED
  total_price    DECIMAL(19,2) NULL,
  currency       VARCHAR(10) NOT NULL DEFAULT 'INR',
  remarks        TEXT NULL,
  cancel_reason  TEXT NULL,
  ordered_by     INT NULL,
  ordered_at     DATETIME NULL,
  received_by    INT NULL,
  received_at    DATETIME NULL,
  created_by     INT NULL,
  updated_by     INT NULL,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_po_ticket  FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
  CONSTRAINT fk_po_product FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_po_tp      FOREIGN KEY (ticket_part_id) REFERENCES ticket_parts(ticket_part_id),
  CONSTRAINT fk_po_ordered FOREIGN KEY (ordered_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_po_recv    FOREIGN KEY (received_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_po_cby     FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_po_uby     FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  KEY idx_po_ticket (ticket_id),
  KEY idx_po_status (status)
);

CREATE TABLE parts_master (
  individual_part_id   INT AUTO_INCREMENT PRIMARY KEY,
  order_id             INT NULL,
  part_cat_id          INT NOT NULL,
  ticket_id            INT NULL,
  part_sr_no           VARCHAR(150) NULL,
  barcode              VARCHAR(150) NULL,
  returned_flag        TINYINT(1) NOT NULL DEFAULT 0,
  damaged_flag         TINYINT(1) NOT NULL DEFAULT 0,
  source               VARCHAR(20) NOT NULL, -- VENDOR | MARKET | STOCK
  vendor_damage_return TINYINT(1) NOT NULL DEFAULT 0,
  replaced_id          INT NULL,
  received             TINYINT(1) NOT NULL DEFAULT 0,
  received_at          DATETIME NULL,
  received_by          INT NULL,
  ordered_by           INT NULL,
  remarks              TEXT NULL,
  is_active            TINYINT(1) NOT NULL DEFAULT 1,
  created_by           INT NULL,
  updated_by           INT NULL,
  created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_pm_order    FOREIGN KEY (order_id) REFERENCES parts_order(order_id),
  CONSTRAINT fk_pm_product  FOREIGN KEY (part_cat_id) REFERENCES product_list(part_cat_id),
  CONSTRAINT fk_pm_ticket   FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
  CONSTRAINT fk_pm_replaced FOREIGN KEY (replaced_id) REFERENCES parts_master(individual_part_id),
  CONSTRAINT fk_pm_recv     FOREIGN KEY (received_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_pm_ordered  FOREIGN KEY (ordered_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_pm_cby      FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_pm_uby      FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  KEY idx_pm_order (order_id),
  KEY idx_pm_ticket (ticket_id),
  KEY idx_pm_sr (part_sr_no),
  KEY idx_pm_source (source)
);

CREATE TABLE vendor_damage_part_return (
  vendor_damage_part_return_id INT AUTO_INCREMENT PRIMARY KEY,
  individual_part_id INT NOT NULL,
  ticket_id          INT NULL,
  return_part_sr_no  VARCHAR(150) NULL,
  remark             TEXT NULL,
  created_by         INT NULL,
  updated_by         INT NULL,
  created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_vdpr_part   FOREIGN KEY (individual_part_id) REFERENCES parts_master(individual_part_id),
  CONSTRAINT fk_vdpr_ticket FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id),
  CONSTRAINT fk_vdpr_cby    FOREIGN KEY (created_by) REFERENCES Employee(employee_id),
  CONSTRAINT fk_vdpr_uby    FOREIGN KEY (updated_by) REFERENCES Employee(employee_id),
  UNIQUE KEY uk_vdpr_part (individual_part_id)
);
