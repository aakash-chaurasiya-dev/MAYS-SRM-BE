-- Inventory & Parts architecture migration (MySQL)
-- Run manually if spring.jpa.hibernate.ddl-auto is not update

-- Inventory enrichments
ALTER TABLE Inventory
  ADD COLUMN IF NOT EXISTS sku VARCHAR(100) NULL,
  ADD COLUMN IF NOT EXISTS min_stock INT NULL,
  ADD COLUMN IF NOT EXISTS hsn_code VARCHAR(50) NULL,
  ADD COLUMN IF NOT EXISTS is_active TINYINT(1) NOT NULL DEFAULT 1;

-- Brand / device_type no longer used on Inventory (optional cleanup)
-- ALTER TABLE Inventory DROP FOREIGN KEY <fk_brand_name>;
-- ALTER TABLE Inventory DROP COLUMN brand_id;
-- ALTER TABLE Inventory DROP COLUMN device_type;

-- Parts / Orders evolve
ALTER TABLE Parts
  ADD COLUMN IF NOT EXISTS source VARCHAR(20) NULL,
  ADD COLUMN IF NOT EXISTS vendor_id INT NULL,
  ADD COLUMN IF NOT EXISTS unit_cost DECIMAL(19,2) NULL,
  ADD COLUMN IF NOT EXISTS defective_returned TINYINT(1) NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS customer_approved TINYINT(1) NULL,
  ADD COLUMN IF NOT EXISTS used_date DATETIME NULL,
  ADD COLUMN IF NOT EXISTS return_date DATETIME NULL,
  ADD COLUMN IF NOT EXISTS stock_applied TINYINT(1) NULL DEFAULT 0;

-- Migrate old returned -> defective_returned if column exists
UPDATE Parts SET defective_returned = COALESCE(returned, 0) WHERE defective_returned IS NULL OR defective_returned = 0;

-- Drop obsolete columns (optional; uncomment after verify)
-- ALTER TABLE Parts DROP COLUMN returned;
-- ALTER TABLE Parts DROP COLUMN in_stock;

-- Inventory log
CREATE TABLE IF NOT EXISTS inventory_log (
  log_id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  branch_id INT NULL,
  change_qty INT NOT NULL,
  balance_after INT NOT NULL,
  reason VARCHAR(20) NOT NULL,
  order_id INT NULL,
  created_by VARCHAR(100) NULL,
  created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_inv_log_product FOREIGN KEY (product_id) REFERENCES Inventory(product_id),
  CONSTRAINT fk_inv_log_order FOREIGN KEY (order_id) REFERENCES Parts(part_id)
);
