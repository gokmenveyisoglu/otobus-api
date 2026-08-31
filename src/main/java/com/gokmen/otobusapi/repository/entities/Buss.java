package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gokmen.otobusapi.repository.record.Bus.ResponseBus;
import jakarta.persistence.*;

import java.util.List;

@Entity
/*@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder*/
@Table(name = "buss")
public class Buss {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bus_id;

    private int max_traveller;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(name = "driver_buss_id", joinColumns = @JoinColumn(name = "buss_id"), inverseJoinColumns = @JoinColumn(name = "driver_id"))
    private List<Drivers> driver_id;

    private String number_plate;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    //@JsonBackReference
    private SchemaHeader schemaHeader; // header no

    @JsonIgnore
    @ManyToMany
    private List<Travellers> travellers; //traveller no

    @JsonIgnore
    @ManyToOne
    private Route route;

    private boolean active = true;

    public static ResponseBus toResponse(Buss buss) {
        return new ResponseBus(
                buss.getBus_id(),
                buss.getMax_traveller(),
                buss.getNumber_plate(),
                buss.getSchemaHeader(),
                buss.getRoute(),
                buss.isActive()
        );
    }

    public int getBus_id() {
        return bus_id;
    }

    public List<Travellers> getTravellers() {
        return travellers;
    }

    public void setTravellers(List<Travellers> travellers) {
        this.travellers = travellers;
    }

    public SchemaHeader getSchemaHeader() {
        return schemaHeader;
    }

    public void setSchemaHeader(SchemaHeader schemaHeader) {
        this.schemaHeader = schemaHeader;
    }

    public String getNumber_plate() {
        return number_plate;
    }

    public void setNumber_plate(String number_plate) {
        this.number_plate = number_plate;
    }

    public List<Drivers> getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(List<Drivers> driver_id) {
        this.driver_id = driver_id;
    }

    public int getMax_traveller() {
        return max_traveller;
    }

    public void setMax_traveller(int max_traveller) {
        this.max_traveller = max_traveller;
    }

    public void increaseMaxTraveller(){
        this.max_traveller++;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
