package com.jsaillant.db.serie;

import com.jsaillant.db.episode.Episode;
import com.jsaillant.db.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SerieService<S extends Serie> {

    protected abstract SerieDAO<S> getDAO();

    public List<S> listSeries() {
        return getDAO().findAll();
    }

    public S findSerie(Long id) throws EntityNotFoundException {
        S serie = getDAO().getOne(id);
        if (serie == null) {
            throw new EntityNotFoundException("La série numéro " + id + " n'existe pas.");
        }
        return serie;
    }

    public List<Episode> listEpisodes(Long serieId) throws EntityNotFoundException {
        S serie = findSerie(serieId);
        return serie.getEpisodes();
    }

    public Episode findEpisodeBySerieAndNum(long serieId, float num) throws EntityNotFoundException {
        Map<Float, Episode> map = listEpisodes(serieId).stream().collect(Collectors.toMap(e -> e.getNum(), Function.identity()));
        Episode episode = map.get(num);
        if (episode == null) {
            throw new EntityNotFoundException("L'épisode numéro " + num + " n'existe pas.");
        }
        return episode;
    }

    public abstract S createSerie();

    public void update(S serie) throws EntityNotFoundException {
        findSerie(serie.getId());
        getDAO().save(serie);
    }

    public S createOrGet(String name, String path) {
        S serie = createSerie();
        serie.setName(name);
        serie.setPath(path);
        if (!getDAO().existsByName(serie.getName())) {
            if (getDAO().existsByName(serie.getName() + " (incomplet)")) {
                S serieFromDB = getDAO().findByName(serie.getName() + " (incomplet)");
                serieFromDB.setName(serie.getName());
                serieFromDB.setPath(path);
                serie = getDAO().save(serieFromDB);
            } else {
                serie = getDAO().save(serie);
            }
        } else {
            serie = getDAO().findByName(serie.getName());
            serie.setPath(path);
            serie = getDAO().save(serie);
        }
        return serie;
    }

    public List<String> getSerieWithMissingEpisode() {
        List<String> res = new ArrayList<>();
        List<S> series = getDAO().findAll();
        for (S serie: series) {
            float prev = 0;
            List<Episode> episodes = serie.getEpisodes();
            episodes.sort(new Comparator<Episode>() {
                @Override
                public int compare(Episode o1, Episode o2) {
                    return Math.round(o1.getNum()-o2.getNum());
                }
            });
            for (Episode episode: episodes) {
                if (episode.getNum() - prev > 1f) {
                    res.add(serie.getName());
                    break;
                }
                prev = episode.getNum();
            }
        }
        res.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        return res;
    }

    public boolean delete(Long id) throws EntityNotFoundException {
        S serie = findSerie(id);
        try {
            getDAO().delete(serie);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
