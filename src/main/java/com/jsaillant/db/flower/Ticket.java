package com.jsaillant.db.flower;

import com.jsaillant.db.flower.Detail;
import com.jsaillant.db.flower.Status;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Ticket {

    @Id
    @GeneratedValue
    public Long id;

    public String contact;

    @Enumerated(EnumType.STRING)
    public Status status;

    public String dodoCode;

    public String commentaire;

    @OneToMany(mappedBy = "ticket")
    public List<Detail> details;

    @CreatedDate
    public LocalDateTime createdAt;

}
