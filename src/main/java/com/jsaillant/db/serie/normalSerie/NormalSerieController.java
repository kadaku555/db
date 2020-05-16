package com.jsaillant.db.serie.normalSerie;

import com.jsaillant.db.serie.SerieController;
import com.jsaillant.db.serie.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/normal")
@RestController
public class NormalSerieController extends SerieController<NormalSerie> {

    @Autowired
    private NormalSerieService service;

    @Override
    protected SerieService<NormalSerie> getSerieService() {
        return service;
    }
}
