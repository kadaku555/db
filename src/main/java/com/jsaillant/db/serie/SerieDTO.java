package com.jsaillant.db.serie;

import com.jsaillant.db.episode.EpisodeDTO;
import com.jsaillant.db.tag.TagDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SerieDTO {

    private Long id;

    private String name = "";

    private boolean viewed = false;

    private List<TagDTO> tags;

    private List<EpisodeDTO> episodes;

    public SerieDTO() {

    }

    public SerieDTO(Serie serie) {
        id = serie.getId();
        name = serie.getName();
        tags = serie.getTags().stream().map(t -> new TagDTO(t)).collect(Collectors.toList());
        episodes = serie.getEpisodes().stream().map(e -> new EpisodeDTO(e)).collect(Collectors.toList());
        viewed = episodes.stream().allMatch(e -> e.isViewed());
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

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public List<EpisodeDTO> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<EpisodeDTO> episodes) {
        this.episodes = episodes;
    }

    public Serie toModel() {
        Serie serie = new Serie();
        serie.setName(name);
        serie.setTags(tags.stream().map(t -> t.toModel()).collect(Collectors.toList()));
        return serie;
    }
}
