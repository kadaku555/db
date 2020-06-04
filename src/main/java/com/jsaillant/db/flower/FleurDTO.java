package com.jsaillant.db.flower;

public class FleurDTO {

    public Long id;

    public String nom;

    public String couleur;

    public String affichage;

    public Integer clochette;

    public Double tmn;

    public String commentaire;

    public Integer stock;

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
        this.stock = pModel.stock;
    }

    public Fleur toModel() {
        Fleur fleur = new Fleur();
        fleur.id = this.id;
        fleur.nom = this.nom;
        fleur.couleur = this.couleur;
        fleur.clochette = this.clochette;
        fleur.tmn = this.tmn;
        fleur.commentaire = this.commentaire;
        fleur.stock = this.stock;
        return fleur;
    }
}
