package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    @ManyToOne(fetch = FetchType.LAZY)
    private Buss busId;

    @JsonIgnore
    private int seat;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Voyages voyageId;

    @JsonIgnore
    private int firstStation;

    @JsonIgnore
    private int lastStation;

    @JsonIgnore
    private Date travelStart;

    @JsonIgnore
    private Date travelEnd;

    private boolean active;

    /*@JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private List<Voyages> voyage_id;*/

                                                            //Voyage ataması ekle

}
