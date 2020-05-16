package com.jsaillant.db.serie.specialSerie;

import com.jsaillant.db.serie.Serie;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("special")
public class SpecialSerie extends Serie {
}
