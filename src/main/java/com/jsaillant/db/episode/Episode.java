package com.jsaillant.db.episode;

import com.jsaillant.db.serie.Serie;

import javax.persistence.*;
import java.io.File;

@Entity
@Table(name = "episode")
public class Episode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private float num = 0;

    @ManyToOne
    private Serie serie;

    private boolean viewed = false;

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

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public String getPath() {
        return getSerie().getPath() + File.separator + getSerie().getName() + File.separator + getName() + ".mp4";
    }
}
