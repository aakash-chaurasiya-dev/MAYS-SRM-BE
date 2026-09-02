package com.mays.srm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaMigrator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void migrate() {
        System.out.println("====== RUNNING CUSTOM SCHEMA MIGRATOR ======");

        try {
            // 1. Extend Enquiry Table
            jdbcTemplate.execute("ALTER TABLE Enquiry " +
                "ADD COLUMN IF NOT EXISTS device_type_id INT NULL AFTER serial_no, " +
                "ADD COLUMN IF NOT EXISTS model_id INT NULL AFTER brand_id, " +
                "ADD COLUMN IF NOT EXISTS custom_model_name VARCHAR(255) NULL AFTER model_id, " +
                "ADD COLUMN IF NOT EXISTS converted_ticket_id INT NULL AFTER status_id, " +
                "ADD COLUMN IF NOT EXISTS is_converted TINYINT(1) DEFAULT 0 AFTER converted_ticket_id, " +
                "ADD COLUMN IF NOT EXISTS customer_name VARCHAR(150) NULL, " +
                "ADD COLUMN IF NOT EXISTS mobile_no VARCHAR(20) NULL, " +
                "ADD COLUMN IF NOT EXISTS email_id VARCHAR(100) NULL, " +
                "ADD COLUMN IF NOT EXISTS address TEXT NULL");
                
            try {
                jdbcTemplate.execute("ALTER TABLE Enquiry ADD CONSTRAINT fk_enquiry_device_type FOREIGN KEY (device_type_id) REFERENCES device_type(device_type_id)");
            } catch(Exception e) { System.out.println("FK device_type exists or error: " + e.getMessage()); }
            
            try {
                jdbcTemplate.execute("ALTER TABLE Enquiry ADD CONSTRAINT fk_enquiry_device_model FOREIGN KEY (model_id) REFERENCES device_model(model_id)");
            } catch(Exception e) { System.out.println("FK device_model exists or error: " + e.getMessage()); }

            try {
                jdbcTemplate.execute("ALTER TABLE Enquiry ADD CONSTRAINT fk_enquiry_converted_ticket FOREIGN KEY (converted_ticket_id) REFERENCES Ticket(ticket_id)");
            } catch(Exception e) { System.out.println("FK ticket exists or error: " + e.getMessage()); }
            
            System.out.println("--- Enquiry table updated ---");

            // 2. Create Inward Record Table
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS inward_record (" +
                "inward_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "serial_no VARCHAR(100) NOT NULL, " +
                "device_type_id INT NULL, " +
                "brand_id INT NULL, " +
                "model_id INT NULL, " +
                "custom_model_name VARCHAR(255) NULL, " +
                "inward_remarks TEXT NULL, " +
                "created_by_employee_id INT NULL, " +
                "created_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "CONSTRAINT fk_inward_user FOREIGN KEY (user_id) REFERENCES User_Master(user_id), " +
                "CONSTRAINT fk_inward_brand FOREIGN KEY (brand_id) REFERENCES brand(brand_id), " +
                "CONSTRAINT fk_inward_model FOREIGN KEY (model_id) REFERENCES device_model(model_id)" +
                ")");
            System.out.println("--- inward_record table created ---");

            // 3. Create Outward Record Table
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS outward_record (" +
                "outward_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "ticket_id INT NOT NULL, " +
                "user_id INT NOT NULL, " +
                "serial_no VARCHAR(100) NOT NULL, " +
                "outward_status VARCHAR(50) DEFAULT 'COMPLETED', " +
                "outward_remarks TEXT NULL, " +
                "handover_to_name VARCHAR(150) NULL, " +
                "handover_to_phone VARCHAR(20) NULL, " +
                "created_by_employee_id INT NULL, " +
                "created_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "CONSTRAINT fk_outward_ticket FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id), " +
                "CONSTRAINT fk_outward_user FOREIGN KEY (user_id) REFERENCES User_Master(user_id)" +
                ")");
            System.out.println("--- outward_record table created ---");

            // 4. Extend user_entry_report Table
            jdbcTemplate.execute("ALTER TABLE user_entry_report " +
                "ADD COLUMN IF NOT EXISTS entry_type VARCHAR(30) NULL AFTER reason, " +
                "ADD COLUMN IF NOT EXISTS enquiry_id INT NULL AFTER entry_type, " +
                "ADD COLUMN IF NOT EXISTS ticket_id INT NULL AFTER enquiry_id, " +
                "ADD COLUMN IF NOT EXISTS inward_id INT NULL AFTER ticket_id, " +
                "ADD COLUMN IF NOT EXISTS outward_id INT NULL AFTER inward_id");

            try {
                jdbcTemplate.execute("ALTER TABLE user_entry_report ADD CONSTRAINT fk_uer_enquiry FOREIGN KEY (enquiry_id) REFERENCES Enquiry(enquiry_id) ON DELETE SET NULL");
            } catch(Exception e) { System.out.println("FK uer_enquiry exists or error: " + e.getMessage()); }

            try {
                jdbcTemplate.execute("ALTER TABLE user_entry_report ADD CONSTRAINT fk_uer_ticket FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id) ON DELETE SET NULL");
            } catch(Exception e) { System.out.println("FK uer_ticket exists or error: " + e.getMessage()); }

            try {
                jdbcTemplate.execute("ALTER TABLE user_entry_report ADD CONSTRAINT fk_uer_inward FOREIGN KEY (inward_id) REFERENCES inward_record(inward_id) ON DELETE SET NULL");
            } catch(Exception e) { System.out.println("FK uer_inward exists or error: " + e.getMessage()); }

            try {
                jdbcTemplate.execute("ALTER TABLE user_entry_report ADD CONSTRAINT fk_uer_outward FOREIGN KEY (outward_id) REFERENCES outward_record(outward_id) ON DELETE SET NULL");
            } catch(Exception e) { System.out.println("FK uer_outward exists or error: " + e.getMessage()); }

            System.out.println("--- user_entry_report table updated ---");
            
            System.out.println("====== CUSTOM SCHEMA MIGRATOR COMPLETED SUCCESSFULLY ======");

        } catch (Exception e) {
            System.err.println("MIGRATION ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
