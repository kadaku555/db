package com.example.demo.dto.flower;

import com.example.demo.models.flower.Fleur;

public class FleurDTO {

    public Long id;

    public String nom;

    public String couleur;

    public String affichage;

    public Integer clochette;

    public Integer tmn;

    public String commentaire;

    public FleurDTO() {

    }

    public FleurDTO(final Fleur pModel) {
        this.id = pModel.id;
        this.nom = pModel.nom;
        this.couleur = pModel.couleur;
        this.affichage = nom + " " + couleur;
        this.clochette = pModel.clochette;
        this.tmn = pModel.tmn;
        this.commentaire = pModel.commentaire;
    }

    public Fleur toModel() {
        Fleur fleur = new Fleur();
        fleur.id = this.id;
        fleur.nom = this.nom;
        fleur.couleur = this.couleur;
        fleur.clochette = this.clochette;
        fleur.tmn = this.tmn;
        fleur.commentaire = this.commentaire;
        return fleur;
    }
}
