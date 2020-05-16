package com.jsaillant.db.tag;

import com.jsaillant.db.exception.EntityAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagDAO tagDAO;

    public List<Tag> list() {
        return tagDAO.findAll();
    }

    public Tag create(String name) throws EntityAlreadyExistsException {
        if (tagDAO.existsByName(name)) {
            throw new EntityAlreadyExistsException("Le tag " + name + " existe déjà.");
        }
        Tag tag = new Tag();
        tag.setName(name);
        return tagDAO.save(tag);
    }

    public Tag createOrGet(String name) {
        try {
            return create(name);
        } catch (EntityAlreadyExistsException e) {
            return tagDAO.findByName(name);
        }
    }
}
