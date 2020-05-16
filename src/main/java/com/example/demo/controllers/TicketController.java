package com.example.demo.controllers;

import com.example.demo.dto.flower.FleurDTO;
import com.example.demo.dto.flower.TicketDTO;
import com.example.demo.exception.EntityNotAllowedException;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.models.flower.Ticket;
import com.example.demo.services.flower.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @RequestMapping(value = "/ticket", produces = "Application/json", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody final TicketDTO pModel) {
        ResponseEntity response;
        if (pModel == null) {
            response = new ResponseEntity<>("Aucune donnée transmise.", HttpStatus.BAD_REQUEST);
        } else {
            String error = pModel.validate();
            if (!StringUtils.isEmpty(error)) {
                response = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            } else {
                try {
                    response = new ResponseEntity<>(new TicketDTO(ticketService.create(pModel.toModel())), HttpStatus.CREATED);
                } catch (Exception e) {
                    response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }
        }
        return response;
    }

    @RequestMapping(value = "/ticket/{id}", produces = "Application/json", method = RequestMethod.PUT)
    public ResponseEntity update(@PathVariable("id") final Long pId, @RequestBody final TicketDTO pModel) {
        ResponseEntity response;
        if (pId == null) {
            response = new ResponseEntity<>("Aucun identifiant transmis.", HttpStatus.BAD_REQUEST);
        } else if (pModel == null) {
            response = new ResponseEntity<>("Aucune donnée transmise.", HttpStatus.BAD_REQUEST);
        } else {
            String error = pModel.validate();
            if (!StringUtils.isEmpty(error)) {
                response = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            } else {
                try {
                    response = new ResponseEntity<>(new TicketDTO(ticketService.update(pId, pModel.toModel())), HttpStatus.OK);
                } catch (Exception e) {
                    response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }
        }
        return response;
    }

    @RequestMapping(value = "/ticket/{id}", produces = "Application/json", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable("id") final Long pId) {
        ResponseEntity response;
        if (pId == null) {
            response = new ResponseEntity<>("Aucun identifiant transmis.", HttpStatus.BAD_REQUEST);
        } else {
            try {
                response = new ResponseEntity<>(new TicketDTO(ticketService.get(pId)), HttpStatus.OK);
            } catch (EntityNotFoundException e) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
        return response;
    }

    @RequestMapping(value = "/ticket/{id}/next", produces = "Application/json", method = RequestMethod.PUT)
    public ResponseEntity goToNextStatus(@PathVariable("id") final Long pId) {
        ResponseEntity response;
        if (pId == null) {
            response = new ResponseEntity<>("Aucun identifiant transmis.", HttpStatus.BAD_REQUEST);
        } else {
            try {
                response = new ResponseEntity<>(new TicketDTO(ticketService.goToNextStatus(pId)), HttpStatus.OK);
            } catch (EntityNotFoundException e) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            } catch (EntityNotAllowedException e) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
        return response;
    }

    @RequestMapping(value = "/ticket/{id}/prev", produces = "Application/json", method = RequestMethod.PUT)
    public ResponseEntity goToPrevStatus(@PathVariable("id") final Long pId) {
        ResponseEntity response;
        if (pId == null) {
            response = new ResponseEntity<>("Aucun identifiant transmis.", HttpStatus.BAD_REQUEST);
        } else {
            try {
                response = new ResponseEntity<>(new TicketDTO(ticketService.goToPrevStatus(pId)), HttpStatus.OK);
            } catch (EntityNotFoundException e) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            } catch (EntityNotAllowedException e) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
        return response;
    }

    @RequestMapping(value = "/tickets", produces = "Application/json")
    public ResponseEntity list() {
        List<TicketDTO> res = new ArrayList<>();
        List<Ticket> tickets = ticketService.list();
        for (Ticket t : tickets) {
            TicketDTO tdto = new TicketDTO();
            tdto.id = t.id;
            tdto.contact = t.contact;
            tdto.status = t.status.name();
            res.add(tdto);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/ticket/{id}", produces = "Application/json", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") final Long pId) {
        ResponseEntity response;
        if (pId == null) {
            response = new ResponseEntity<>("Aucun identifiant transmis.", HttpStatus.BAD_REQUEST);
        } else {
            try {
                ticketService.delete(pId);
                response = new ResponseEntity<>("", HttpStatus.NO_CONTENT);
            } catch (EntityNotFoundException e) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }

    @RequestMapping(value = "/fleurs", produces = "Application/json")
    public ResponseEntity listFleur() {
        return new ResponseEntity<>(ticketService.listFleur().stream().map(f -> new FleurDTO(f)).collect(Collectors.toList()), HttpStatus.OK);
    }
}
