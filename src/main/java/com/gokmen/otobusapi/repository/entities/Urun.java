package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class Urun {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String urunAdi;

    private int adet;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Katagory katagories;

    @ManyToMany
    @JsonIgnore
    private List<OrderList> orderLists;

}
