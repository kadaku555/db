package com.example.demo.services;

import com.example.demo.dao.EpisodeDAO;
import com.example.demo.dao.SerieDAO;
import com.example.demo.dao.TagDAO;
import com.example.demo.models.Episode;
import com.example.demo.models.Serie;
import com.example.demo.models.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnimeService {

    @Autowired
    private TagDAO tagDAO;

    @Autowired
    private SerieDAO serieDAO;

    @Autowired
    private EpisodeDAO episodeDAO;

    public List<Tag> listTags() {
        return tagDAO.findAll();
    }

    public List<Serie> listSeries() {
        return serieDAO.findAll();
    }

    public Serie findSerie(Long id) {
        Serie serie = serieDAO.getOne(id);
        if (serie == null) {
            throw new EntityNotFoundException("La série numéro " + id + " n'existe pas.");
        }
        return serie;
    }

    public List<Episode> listEpisodes(Long serieId) {
        Serie serie = findSerie(serieId);
        return serie.getEpisodes();
    }

    public Episode findEpisode(Long id) {
        Episode episode = episodeDAO.getOne(id);
        if (episode == null) {
            throw new EntityNotFoundException("L'épisode numéro " + id + " n'existe pas.");
        }
        return episode;
    }

    public Episode findEpisodeBySerieAndNum(long serieId, float num) {
        Map<Float, Episode> map = listEpisodes(serieId).stream().collect(Collectors.toMap(e -> e.getNum(), Function.identity()));
        Episode episode = map.get(num);
        if (episode == null) {
            throw new EntityNotFoundException("L'épisode numéro " + num + " n'existe pas.");
        }
        return episode;
    }

    public void importFromFileSystem(String path) {
        File origin = new File(path);
        if (!origin.isDirectory()) {
            throw new IllegalArgumentException("Le chemin doit correspondre à un répertoire.");
        }
        for (File dossSerie : origin.listFiles()) {
            if (dossSerie.isDirectory()) {
                Serie serie = new Serie();
                serie.setName(dossSerie.getName());
                if (!serieDAO.existsByName(serie.getName())) {
                    if (serieDAO.existsByName(serie.getName() + " (incomplet)")) {
                        serie = serieDAO.findByName(serie.getName() + " (incomplet)");
                    } else {
                        serie = serieDAO.save(serie);
                    }
                }
                for (File dossEpisode : dossSerie.listFiles()) {
                    try {
                        Episode episode = new Episode();
                        episode.setName(dossEpisode.getName().substring(0, dossEpisode.getName().lastIndexOf(".")));
                        if (!episodeDAO.existsByName(episode.getName())) {
                            String[] split = episode.getName().split(" - ");
                            episode.setNum(Float.parseFloat(split[split.length - 1]));
                            episode.setPath(dossEpisode.getAbsolutePath());
                            episode.setSerie(serie);
                            episodeDAO.save(episode);
                        }
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Issue with Serie: " + dossSerie.getName() + ", Episode: " + dossEpisode.getName());
                    }
                }
            }
        }
    }

    public void update(Serie serie) {
        findSerie(serie.getId());
        serieDAO.save(serie);
    }

    public void update(Episode episode) {
        findEpisode(episode.getId());
        episodeDAO.save(episode);
    }
}
