package com.jsaillant.db.flower;

import com.jsaillant.db.flower.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketDAO extends JpaRepository<Ticket, Long> {

}
