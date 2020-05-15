package com.example.demo.dao.flower;

import com.example.demo.models.flower.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailDAO extends JpaRepository<Detail, Long> {
}
