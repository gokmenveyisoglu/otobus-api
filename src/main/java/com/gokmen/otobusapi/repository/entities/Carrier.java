package com.gokmen.otobusapi.repository.entities;

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
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int carrierId;

    private String carrierName;
    private String logoUrl;
    private String contractPhone;
    private String contractEmail;

    private boolean active;

    @OneToMany(mappedBy = "carrier")
    private List<Buss> busses;
}
