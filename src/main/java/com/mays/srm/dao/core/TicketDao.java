package com.mays.srm.dao.core;
import com.mays.srm.organization.entities.Branch;
import com.mays.srm.ticket.repository.custom.TicketDaoCustom;
import com.mays.srm.ticket.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketDao extends JpaRepository<Ticket, Integer>, TicketDaoCustom {
    
    // getTicket(int Id) -> Use built-in findById(Integer id) from JpaRepository
    // getAllTicket() -> Use built-in findAll() from JpaRepository

    // + getAllTicketOfUser(String user_ref_no)
    List<Ticket> findByUserMasterUserId(Integer userId);

    // + getAllTicketOfBranch(int Branch)
    List<Ticket> findByTicketBranchBranchId(Integer branchId);

    // + getAllTicketOfStatus(int status)
    List<Ticket> findByTicketStatusStatusId(Integer statusId);

    // + getAllTicketOfEmployee(int emp_id)
    List<Ticket> findByEmployeeEmployeeId(Integer employeeId);
}
