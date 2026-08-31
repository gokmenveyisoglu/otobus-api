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
    private int busId;

    private int maxTraveller;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(name = "driver_buss_id", joinColumns = @JoinColumn(name = "buss_id"), inverseJoinColumns = @JoinColumn(name = "driver_id"))
    private List<Drivers> driverId;

    private String numberPlate;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    //@JsonBackReference
    private SchemaHeader schemaHeader; // header no

    @JsonIgnore
    @ManyToOne
    private Route route;

    private boolean active = true;

    public static ResponseBus toResponse(Buss buss) {
        return new ResponseBus(
                buss.getBusId(),
                buss.getMaxTraveller(),
                buss.getNumberPlate(),
                buss.getSchemaHeader(),
                buss.getRoute(),
                buss.isActive()
        );
    }

    public int getBusId() {
        return busId;
    }

    public SchemaHeader getSchemaHeader() {
        return schemaHeader;
    }

    public void setSchemaHeader(SchemaHeader schemaHeader) {
        this.schemaHeader = schemaHeader;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String number_plate) {
        this.numberPlate = number_plate;
    }

    public List<Drivers> getDriverId() {
        return driverId;
    }

    public void setDriverId(List<Drivers> driver_id) {
        this.driverId = driver_id;
    }

    public int getMaxTraveller() {
        return maxTraveller;
    }

    public void setMaxTraveller(int max_traveller) {
        this.maxTraveller = max_traveller;
    }

    public void increaseMaxTraveller(){
        this.maxTraveller++;
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
