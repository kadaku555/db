package com.jsaillant.db.tag;

public class TagDTO {

    private Long id;

    private String name = "";

    public TagDTO() {

    }

    public TagDTO(Tag t) {
        this.id = t.getId();
        this.name = t.getName();
    }

    public Tag toModel() {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
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
}
