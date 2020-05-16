package com.jsaillant.db.tag;

import com.jsaillant.db.serie.Serie;

import javax.persistence.*;
import java.util.List;

@Entity
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "serie_tag",
            joinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "serie_id", referencedColumnName = "id")})
    private List<Serie> series;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
