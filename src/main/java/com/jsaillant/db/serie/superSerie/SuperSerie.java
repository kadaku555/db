package com.jsaillant.db.serie.superSerie;

import com.jsaillant.db.serie.Serie;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("super")
public class SuperSerie extends Serie {
}
