package com.example.demo.dto.flower;

import com.example.demo.models.flower.Detail;
import com.example.demo.models.flower.Fleur;

public class DetailDTO {

    public Long id;

    public Integer quantite;

    public String fleur;

    public DetailDTO() {

    }

    public DetailDTO(final Detail pModel) {
        this.id = pModel.id;
        this.quantite = pModel.quantite;
        if (pModel.fleur != null) {
            this.fleur = ""+pModel.fleur.id;
        }
    }

    public Detail toModel() {
        Detail detail = new Detail();
        detail.id = this.id;
        detail.quantite = this.quantite;
        if (this.fleur != null) {
            detail.fleur = new Fleur();
            detail.fleur.id = Long.parseLong(this.fleur);
        }
        return detail;
    }
}
