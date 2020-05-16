package com.jsaillant.db.episode;

import com.jsaillant.db.exception.EntityNotFoundException;
import com.jsaillant.db.serie.Serie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EpisodeService {

    @Autowired
    private EpisodeDAO episodeDAO;

    public Episode find(Long id) throws EntityNotFoundException {
        Episode episode = episodeDAO.getOne(id);
        if (episode == null) {
            throw new EntityNotFoundException("L'épisode numéro " + id + " n'existe pas.");
        }
        return episode;
    }

    public void update(Episode episode) throws EntityNotFoundException {
        find(episode.getId());
        episodeDAO.save(episode);
    }

    public Episode createOrGet(String name, String path, Serie serie) {
        Episode episode = new Episode();
        episode.setName(name.substring(0, name.lastIndexOf(".")));
        if (!episodeDAO.existsByName(episode.getName())) {
            String[] split = episode.getName().split(" - ");
            episode.setNum(Float.parseFloat(split[split.length - 1]));
            episode.setSerie(serie);
            episodeDAO.save(episode);
        }
        return episode;
    }

    public boolean delete(Long id) throws EntityNotFoundException {
        Episode episode = find(id);
        try {
            episodeDAO.delete(episode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
