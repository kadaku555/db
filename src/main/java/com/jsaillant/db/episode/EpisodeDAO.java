package com.jsaillant.db.episode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeDAO extends JpaRepository<Episode, Long> {
    boolean existsByName(String name);

}
