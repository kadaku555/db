package com.example.demo.controllers;

import com.example.demo.dto.EpisodeDTO;
import com.example.demo.dto.SerieDTO;
import com.example.demo.dto.TagDTO;
import com.example.demo.models.Episode;
import com.example.demo.models.Serie;
import com.example.demo.services.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    @RequestMapping(value = "/series", produces = "Application/json")
    public List<SerieDTO> listSerie() {
        return animeService.listSeries().stream().map(s -> new SerieDTO(s)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/serie/{id}", produces = "Application/json", method = RequestMethod.GET)
    public ResponseEntity findSerie(@PathVariable("id") Long id) {
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(new SerieDTO(animeService.findSerie(id)), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/episodes/{serieId}", produces = "Application/json")
    public ResponseEntity listEpisodes(@PathVariable("serieId") Long serieId) {
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(animeService.listEpisodes(serieId).stream().map(e -> new EpisodeDTO(e)).collect(Collectors.toList()), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/episode/{id}", produces = "Application/json", method = RequestMethod.GET)
    public ResponseEntity findEpisode(@PathVariable("id") Long id) {
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(new EpisodeDTO(animeService.findEpisode(id)), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/episode/{serieId}/{num}", produces = "Application/json")
    public ResponseEntity findEpisodeBySerieAndNum(@PathVariable("serieId") Long serieId, @PathVariable("num") Float num) {
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(new EpisodeDTO(animeService.findEpisodeBySerieAndNum(serieId, num)), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/episode/{id}/video", produces = "video/mp4")
    public FileSystemResource getvideo(@PathVariable("id") Long id) {
        return new FileSystemResource(new File(animeService.findEpisode(id).getPath()));
    }

    @RequestMapping(value = "/serie/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateSerie(@PathVariable("id") Long id, @RequestBody SerieDTO serieDTO) {
        ResponseEntity response;
        try {
            Serie serieFromDB = animeService.findSerie(id);
            Serie serie = serieDTO.toModel();
            serieFromDB.setName(serie.getName());
            serieFromDB.setTags(serie.getTags());
            serieFromDB.setViewed(serie.isViewed());
            animeService.update(serieFromDB);
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/episode/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateEpisode(@PathVariable("id") Long id, @RequestBody EpisodeDTO episode) {
        ResponseEntity response;
        try {
            Episode episodeFromDB = animeService.findEpisode(id);
            episodeFromDB.setName(episode.getName());
            episodeFromDB.setViewed(episode.isViewed());
            animeService.update(episodeFromDB);
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/tags", produces = "Application/json")
    public List<TagDTO> listTags() {
        return animeService.listTags().stream().map(t -> new TagDTO(t)).collect(Collectors.toList());
    }

    @RequestMapping("/import")
    public void importFromFileSystem(@RequestParam("path") String path) {
        animeService.importFromFileSystem(path);
    }
}
