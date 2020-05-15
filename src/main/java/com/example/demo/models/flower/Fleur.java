package com.example.demo.models.flower;

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

    public Integer tmn;

    public String commentaire;
}
