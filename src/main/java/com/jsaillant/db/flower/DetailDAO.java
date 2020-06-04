package com.jsaillant.db.flower;

import com.jsaillant.db.flower.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailDAO extends JpaRepository<Detail, Long> {
}
