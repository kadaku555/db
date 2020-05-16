package com.jsaillant.db.serie;

import com.jsaillant.db.episode.EpisodeService;
import com.jsaillant.db.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SerieController<S extends Serie> {

    @Autowired
    private EpisodeService episodeService;

    protected EpisodeService getEpisodeService() {
        return episodeService;
    }

    protected abstract SerieService<S> getSerieService();

    @RequestMapping(value = "/series", produces = "Application/json")
    public List<SerieDTO> list() {
        return getSerieService().listSeries().stream().map(s -> new SerieDTO(s)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/serie/{id}", produces = "Application/json", method = RequestMethod.GET)
    public ResponseEntity find(@PathVariable("id") Long id) {
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(new SerieDTO(getSerieService().findSerie(id)), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/serie/{id}", method = RequestMethod.PUT)
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody SerieDTO serieDTO) {
        ResponseEntity response;
        try {
            S serieFromDB = getSerieService().findSerie(id);
            Serie serie = serieDTO.toModel();
            serieFromDB.setName(serie.getName());
            serieFromDB.setTags(serie.getTags());
            getSerieService().update(serieFromDB);
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping("/import")
    public void importFromFileSystem(@RequestParam("path") String path) {
        File origin = new File(path);
        if (!origin.isDirectory()) {
            throw new IllegalArgumentException("Le chemin doit correspondre à un répertoire.");
        }
        for (File dossSerie : origin.listFiles()) {
            if (dossSerie.isDirectory()) {
                S serie = getSerieService().createOrGet(dossSerie.getName(), path);
                for (File dossEpisode : dossSerie.listFiles()) {
                    try {
                        getEpisodeService().createOrGet(dossEpisode.getName(), dossEpisode.getAbsolutePath(), serie);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Issue with Serie: " + dossSerie.getName() + ", Episode: " + dossEpisode.getName(), e);
                    }
                }
            }
        }
    }

    @RequestMapping("/missing/episodes")
    public List<String> getSerieWithMissingEpisode() {
        return getSerieService().getSerieWithMissingEpisode();
    }

    @RequestMapping(value = "/serie/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") Long id) {
        ResponseEntity response;
        try {
            if (getSerieService().delete(id)) {
                response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                response = new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
