package com.jsaillant.db.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagDAO extends JpaRepository<Tag, Long> {
    boolean existsByName(String name);

    Tag findByName(String name);
}
