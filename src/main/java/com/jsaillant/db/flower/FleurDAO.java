package com.jsaillant.db.flower;

import com.jsaillant.db.flower.Fleur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FleurDAO extends JpaRepository<Fleur, Long> {
}
