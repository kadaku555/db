package com.jsaillant.db.episode;

import com.jsaillant.db.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
public class EpisodeController {

    @Autowired
    private EpisodeService episodeService;

    @RequestMapping(value = "/episode/{id}", produces = "Application/json", method = RequestMethod.GET)
    public ResponseEntity find(@PathVariable("id") Long id) {
        ResponseEntity response;
        try {
            response = new ResponseEntity<>(new EpisodeDTO(episodeService.find(id)), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/episode/{id}/video", produces = "video/mp4")
    public FileSystemResource getvideo(@PathVariable("id") Long id) throws EntityNotFoundException {
        return new FileSystemResource(new File(episodeService.find(id).getPath()));
    }

    @RequestMapping(value = "/episode/{id}", method = RequestMethod.PUT)
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody EpisodeDTO episode) {
        ResponseEntity response;
        try {
            Episode episodeFromDB = episodeService.find(id);
            episodeFromDB.setName(episode.getName());
            episodeFromDB.setViewed(episode.isViewed());
            episodeService.update(episodeFromDB);
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/episode/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") Long id) {
        ResponseEntity response;
        try {
            if (episodeService.delete(id)) {
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
