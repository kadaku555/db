package com.example.demo.dao;

import com.example.demo.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagDAO extends JpaRepository<Tag, Long> {
    boolean existsByName(String name);
}
