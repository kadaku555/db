package com.example.demo.services.flower;

import com.example.demo.dao.flower.DetailDAO;
import com.example.demo.dao.flower.FleurDAO;
import com.example.demo.dao.flower.TicketDAO;
import com.example.demo.exception.EntityNotAllowedException;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.models.flower.Detail;
import com.example.demo.models.flower.Fleur;
import com.example.demo.models.flower.Status;
import com.example.demo.models.flower.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketDAO ticketDAO;

    @Autowired
    private DetailDAO detailDAO;

    @Autowired
    private FleurDAO fleurDAO;

    public Ticket create(final Ticket pModel) {
        List<Detail> details = pModel.details;
        pModel.status = Status.NEW;
        pModel.createdAt = LocalDateTime.now();
        Ticket savedModel = ticketDAO.save(pModel);
        savedModel.details = new ArrayList<>();
        for (Detail d : details) {
            d.ticket = savedModel;
            d.fleur = fleurDAO.findById(d.fleur.id).get();
            Detail savedDetail = detailDAO.save(d);
            savedModel.details.add(savedDetail);
        }
        return savedModel;
    }

    public Ticket update(final Long pId, final Ticket pModel) throws EntityNotFoundException {
        Ticket modelFromDB = get(pId);
        modelFromDB.contact = pModel.contact;
        modelFromDB.dodoCode = pModel.dodoCode;
        modelFromDB.commentaire = pModel.commentaire;
        Ticket savedModel = ticketDAO.save(modelFromDB);
        detailDAO.deleteAll(modelFromDB.details);
        List<Detail> details = pModel.details;
        savedModel.details = new ArrayList<>();
        for (Detail d : details) {
            d.ticket = savedModel;
            d.fleur = fleurDAO.findById(d.fleur.id).get();
            Detail savedDetail = detailDAO.save(d);
            savedModel.details.add(savedDetail);
        }
        return savedModel;
    }

    public Ticket goToNextStatus(final Long pId) throws EntityNotFoundException, EntityNotAllowedException {
        Ticket modelFromDB = get(pId);
        switch (modelFromDB.status) {
            case NEW:
                modelFromDB.status = Status.TODO;
                break;
            case TODO:
                modelFromDB.status = Status.DONE;
                break;
            case DONE:
                modelFromDB.status = Status.RELEASED;
                break;
            case RELEASED:
                throw new EntityNotAllowedException("Le Statut RELEASED est final.");
        }
        return ticketDAO.save(modelFromDB);
    }

    public Ticket goToPrevStatus(final Long pId) throws EntityNotFoundException, EntityNotAllowedException {
        Ticket modelFromDB = get(pId);
        switch (modelFromDB.status) {
            case NEW:
                throw new EntityNotAllowedException("Le Statut NEW est le premier.");
            case TODO:
                modelFromDB.status = Status.NEW;
                break;
            case DONE:
                modelFromDB.status = Status.TODO;
                break;
            case RELEASED:
                modelFromDB.status = Status.DONE;
                break;
        }
        return ticketDAO.save(modelFromDB);
    }

    public Ticket get(Long pId) throws EntityNotFoundException {
        Ticket res = ticketDAO.findById(pId).get();
        if (res == null) {
            throw new EntityNotFoundException("Le ticket " + pId + " n'existe pas.");
        }
        return res;
    }

    public List<Ticket> list() {
        return ticketDAO.findAll();
    }

    public List<Fleur> listFleur() {
        return fleurDAO.findAll();
    }

    public void delete(final Long pId) throws EntityNotFoundException {
        ticketDAO.delete(get(pId));
    }
}
