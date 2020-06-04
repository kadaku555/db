package com.jsaillant.db.flower;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Fleur {

    @Id
    @GeneratedValue
    public Long id;

    public String nom;

    public String couleur;

    public Integer clochette;

    public Double tmn;

    public String commentaire;

    public Integer stock = 0;
}
