package com.jsaillant.db.serie.specialSerie;

import com.jsaillant.db.serie.SerieController;
import com.jsaillant.db.serie.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/special")
@RestController
public class SpecialSerieController extends SerieController<SpecialSerie> {

    @Autowired
    private SpecialSerieService service;

    @Override
    protected SerieService<SpecialSerie> getSerieService() {
        return service;
    }
}
