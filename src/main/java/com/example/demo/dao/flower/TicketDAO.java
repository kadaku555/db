package com.example.demo.dao.flower;

import com.example.demo.models.flower.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketDAO extends JpaRepository<Ticket, Long> {

}
