package com.jsaillant.db.serie.normalSerie;

import com.jsaillant.db.serie.Serie;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("normal")
public class NormalSerie extends Serie {


}
