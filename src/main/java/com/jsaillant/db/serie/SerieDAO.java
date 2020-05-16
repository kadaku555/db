package com.jsaillant.db.serie;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieDAO<S extends Serie> extends JpaRepository<S, Long> {
    boolean existsByName(String name);

    S findByName(String s);
}
