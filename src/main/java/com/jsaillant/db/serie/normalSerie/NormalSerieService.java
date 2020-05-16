package com.jsaillant.db.serie.normalSerie;

import com.jsaillant.db.serie.SerieDAO;
import com.jsaillant.db.serie.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NormalSerieService extends SerieService<NormalSerie> {

    @Autowired
    private NormalSerieDAO dao;

    @Override
    protected SerieDAO<NormalSerie> getDAO() {
        return dao;
    }

    @Override
    public NormalSerie createSerie() {
        return new NormalSerie();
    }
}
