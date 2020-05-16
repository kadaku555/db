package com.jsaillant.db.serie.superSerie;

import com.jsaillant.db.serie.SerieController;
import com.jsaillant.db.serie.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/super")
@RestController
public class SuperSerieController extends SerieController<SuperSerie> {

    @Autowired
    private SuperSerieService service;

    @Override
    protected SerieService<SuperSerie> getSerieService() {
        return service;
    }
}
