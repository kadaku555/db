package com.jsaillant.db.serie;

import com.jsaillant.db.episode.Episode;
import com.jsaillant.db.tag.Tag;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "serie")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE")
public class Serie {

    @Id
    @GeneratedValue
    protected Long id;

    protected String name = "";

    protected String path = "";

    @ManyToMany
    @JoinTable(name = "serie_tag",
            joinColumns = {@JoinColumn(name = "serie_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")})
    protected List<Tag> tags;

    @OneToMany(mappedBy = "serie")
    protected List<Episode> episodes;

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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
