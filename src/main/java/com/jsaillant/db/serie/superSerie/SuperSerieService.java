package com.jsaillant.db.serie.superSerie;

import com.jsaillant.db.serie.SerieDAO;
import com.jsaillant.db.serie.SerieService;
import com.jsaillant.db.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SuperSerieService extends SerieService<SuperSerie> {

    @Autowired
    private SuperSerieDAO dao;

    @Autowired
    private TagService tagService;

    @Override
    protected SerieDAO<SuperSerie> getDAO() {
        return dao;
    }

    @Override
    public SuperSerie createSerie() {
        SuperSerie serie = new SuperSerie();
        serie.setTags(Arrays.asList(tagService.createOrGet("Ecchi")));
        return serie;
    }
}
