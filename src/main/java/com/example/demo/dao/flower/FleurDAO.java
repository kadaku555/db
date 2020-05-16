package com.example.demo.dao.flower;

import com.example.demo.models.flower.Fleur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FleurDAO extends JpaRepository<Fleur, Long> {
}
