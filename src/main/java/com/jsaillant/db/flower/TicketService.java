package com.jsaillant.db.flower;

import com.jsaillant.db.exception.EntityNotAllowedException;
import com.jsaillant.db.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        applyIfStatusChange(modelFromDB, pModel);
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

    private void applyIfStatusChange(final Ticket pModelFromDB, final Ticket pModel) {
        if (pModelFromDB.status != pModel.status) {
            if (pModel.status == Status.RELEASED) {
                for(Detail detail : pModelFromDB.details) {
                    detail.fleur.stock = detail.fleur.stock - detail.quantite;
                    fleurDAO.save(detail.fleur);
                }
            }
            if (pModelFromDB.status == Status.RELEASED) {
                for(Detail detail : pModelFromDB.details) {
                    detail.fleur.stock = detail.fleur.stock + detail.quantite;
                    fleurDAO.save(detail.fleur);
                }
            }
            pModelFromDB.status = pModel.status;
        }
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
                modelFromDB.status = Status.BAG;
                break;
            case BAG:
                modelFromDB.status = Status.RELEASED;
                //maj stock
                for(Detail detail : modelFromDB.details) {
                    detail.fleur.stock = detail.fleur.stock - detail.quantite;
                    fleurDAO.save(detail.fleur);
                }
                break;
            case RELEASED:
                modelFromDB.status = Status.ARCHIVED;
                break;
            case ARCHIVED:
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
            case BAG:
                modelFromDB.status = Status.DONE;
                break;
            case RELEASED:
                modelFromDB.status = Status.BAG;
                //maj stock
                for(Detail detail : modelFromDB.details) {
                    detail.fleur.stock = detail.fleur.stock + detail.quantite;
                    fleurDAO.save(detail.fleur);
                }
                break;
            case ARCHIVED:
                throw new EntityNotAllowedException("Le Statut ARCHIVED ne peut être annulé.");
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
        Ticket t = get(pId);
        detailDAO.deleteAll(t.details);
        ticketDAO.delete(t);
    }
}
