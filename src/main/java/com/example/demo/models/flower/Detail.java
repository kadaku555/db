package com.example.demo.models.flower;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Detail {

    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne
    public Ticket ticket;

    @ManyToOne
    public Fleur fleur;

    public Integer quantite;
}
