package com.example.demo.dto;

import com.example.demo.models.Episode;

public class EpisodeDTO {

    private Long id;

    private String name;

    private float num = 0;

    private boolean viewed = false;

    private String path = "";

    private Long serie;

    public EpisodeDTO() {

    }

    public EpisodeDTO(Episode episode) {
        id = episode.getId();
        name = episode.getName();
        num = episode.getNum();
        viewed = episode.isViewed();
        path = episode.getPath();
        serie = episode.getSerie().getId();
    }

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

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSerie() {
        return serie;
    }

    public void setSerie(Long serie) {
        this.serie = serie;
    }
}
