package com.example.demo.dao;

import com.example.demo.models.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerieDAO extends JpaRepository<Serie, Long> {
    boolean existsByName(String name);

    Serie findByName(String s);
}
