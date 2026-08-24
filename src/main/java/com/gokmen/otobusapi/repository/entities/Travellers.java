package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "travellers")
public class Travellers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int traveller_id;

    private String travellerName;

    private String travellerSurname;

    private String gender;

    @JsonIgnore
    private boolean isForeign;

    private String indentityNumber;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY) // ManyToOne? || ManyToMany
    private List<Buss> bus_id;

    @JsonIgnore
    private int seat;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Voyages voyageNo;

    @JsonIgnore
    private int firstStation;

    @JsonIgnore
    private int lastStation;

    @JsonIgnore
    private Date travelStart;

    @JsonIgnore
    private Date travelEnd;

    /*@JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private List<Voyages> voyage_id;*/

                                                            //Voyage ataması ekle

}
