package com.jsaillant.db.serie.specialSerie;

import com.jsaillant.db.serie.SerieDAO;
import com.jsaillant.db.serie.SerieService;
import com.jsaillant.db.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SpecialSerieService extends SerieService<SpecialSerie> {

    @Autowired
    private SpecialSerieDAO dao;

    @Autowired
    private TagService tagService;

    @Override
    protected SerieDAO<SpecialSerie> getDAO() {
        return dao;
    }

    @Override
    public SpecialSerie createSerie() {
        SpecialSerie serie = new SpecialSerie();
        serie.setTags(Arrays.asList(tagService.createOrGet("Hentai")));
        return serie;
    }
}
