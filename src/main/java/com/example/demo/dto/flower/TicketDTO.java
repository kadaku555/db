package com.example.demo.dto.flower;

import com.example.demo.models.flower.Detail;
import com.example.demo.models.flower.Status;
import com.example.demo.models.flower.Ticket;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TicketDTO {

    public Long id;

    public String contact;

    public String status;

    public String dodoCode;

    public String commentaire;

    public List<DetailDTO> details;


    public TicketDTO(final Ticket pModel) {
        this.id = pModel.id;
        this.contact = pModel.contact;
        this.status = pModel.status.name();
        this.dodoCode = pModel.dodoCode;
        this.commentaire = pModel.commentaire;
        this.details = new ArrayList<>();
        for (Detail d : pModel.details) {
            details.add(new DetailDTO(d));
        }
    }

    public TicketDTO() {

    }

    public String validate() {
        String error = "";
        if (StringUtils.isEmpty(this.contact)) {
            error = "Le nom du contact est obligatoire.";
        }
        return error;
    }

    public Ticket toModel() {
        Ticket ticket = new Ticket();
        ticket.id = this.id;
        ticket.contact = this.contact;
        ticket.status = Status.valueOf(this.status);
        ticket.dodoCode = this.dodoCode;
        ticket.commentaire = this.commentaire;
        ticket.details = new ArrayList<>();
        if (!CollectionUtils.isEmpty(this.details)) {
            for (DetailDTO ddto : this.details) {
                Detail d = ddto.toModel();
                d.ticket = ticket;
                ticket.details.add(d);
            }
        }
        return ticket;
    }
}
