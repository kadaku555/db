package com.example.demo.dao;

import com.example.demo.models.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeDAO extends JpaRepository<Episode, Long> {
    boolean existsByName(String name);

}
