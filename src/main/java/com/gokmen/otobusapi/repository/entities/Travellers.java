package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gokmen.otobusapi.repository.record.Traveller.CreateTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.ResponseTraveller;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Setter
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

    public static Travellers fromCreate(CreateTraveller request, Voyages voyage, List<Travellers> relevantTravellers) {
        if (!voyage.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voyage is not active");
        Buss bus = voyage.getBus();
        if (bus == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voyage does not have a bus");
        if (!bus.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bus is not active");
        if (!seatExists(bus, request.seat()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat does not exists");
        Travellers traveller = new Travellers();
        if (!sameGender(request, voyage, relevantTravellers))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat is adjacent to different gender");
        if (genderLock(request, voyage, relevantTravellers))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The gender lock happened.");

        String travellerName = requiredText(request.travellerName(), "Traveller name is required");
        String travellerSurname = requiredText(request.travellerSurname(), "Traveller surname is required");
        String gender = requiredText(request.gender(), "Gender is required");
        String storedIdentification;

        if (request.isForeign()) {
            storedIdentification = "Foreigner";
        } else {
            String identificationNumber = request.identificationNumber();
            if (identificationNumber == null || !identificationNumber.matches("\\d{11}"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identification number must contain 11 digits");
            if (!validId(identificationNumber))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid identification number");

            String first;
            String last;

            first = request.identificationNumber().substring(0, 2);
            last = request.identificationNumber().substring(9);

            storedIdentification = first + "*******" + last;
        }

        traveller.setTravellerName(travellerName);
        traveller.setTravellerSurname(travellerSurname);
        traveller.setGender(gender);
        traveller.setForeign(request.isForeign());
        traveller.setIndentityNumber(storedIdentification);

        traveller.setBusId(bus);
        traveller.setVoyageId(voyage);
        traveller.setSeat(request.seat());
        traveller.setFirstStation(voyage.getFirstStation());
        traveller.setLastStation(voyage.getLastStation());
        traveller.setTravelStart(voyage.getStartDate());
        traveller.setTravelEnd(voyage.getEndDate());
        traveller.setActive(true);

        return traveller;
    }


    public static ResponseTraveller toResponse(Travellers traveller) {
        return new ResponseTraveller(
                traveller.getTraveller_id(),
                traveller.getTravellerName(),
                traveller.getTravellerSurname(),
                traveller.getGender(),
                traveller.isForeign(),
                traveller.getIndentityNumber(),
                Buss.toResponse(traveller.getBusId()),
                traveller.getSeat(),
                Voyages.toResponse(traveller.getVoyageId()),
                traveller.getFirstStation(),
                traveller.getLastStation(),
                traveller.getTravelStart(),
                traveller.getTravelEnd(),
                traveller.isActive()
        );
    }

    private static boolean validId(String identificationNumber) {
        int[] numbers = new int[11];

        for (int i = 0; i < 11; i++) {
            numbers[i] = Integer.parseInt(identificationNumber.substring(i, (i + 1)));
        }
        boolean condition1 = (numbers[0] + numbers[1] + numbers[2] + numbers[3] + numbers[4] + numbers[5] + numbers[6] + numbers[7] + numbers[8] + numbers[9]) % 10 == numbers[10];
        boolean condition2 = (((numbers[0] + numbers[2] + numbers[4] + numbers[6] + numbers[8]) * 7) + ((numbers[1] + numbers[3] + numbers[5] + numbers[7]) * 9)) % 10 == numbers[9];
        boolean condition3 = ((numbers[0] + numbers[2] + numbers[4] + numbers[6] + numbers[8]) * 8) % 10 == numbers[10];
        return condition1 && condition2 && condition3;
    }

    private static boolean seatExists(Buss buss, int requestedSeat) {
        if (requestedSeat <= 0 || buss.getSchemaHeader() == null) {
            return false;
        }
        return buss.getSchemaHeader().getSchemaDetails().stream().anyMatch(row ->
                row.getColumn1() == requestedSeat || row.getColumn2() == requestedSeat || row.getColumn4() == requestedSeat || row.getColumn5() == requestedSeat
        );
    }

    private boolean segmentsOverlap(int firstStart, int firstEnd, int secondStart, int secondEnd) {
        int normalizedFirstStart = Math.min(firstStart, firstEnd);
        int normalizedFirstEnd = Math.max(firstStart, firstEnd);
        int normalizedSecondStart = Math.min(secondStart, secondEnd);
        int normalizedSecondEnd = Math.max(secondStart, secondEnd);

        return normalizedFirstStart < normalizedSecondEnd && normalizedSecondStart < normalizedFirstEnd;
    }

    private static boolean sameGender(CreateTraveller request, Voyages voyages, List<Travellers> relevantTravellers) {
        String requestedGender = request.gender().trim().toLowerCase(Locale.ROOT);

        if (!requestedGender.equals("erkek") && !requestedGender.equals("kadın"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid gender");

        List<SchemaDetail> schemaDetails = voyages.getBus().getSchemaHeader().getSchemaDetails();

        for (Travellers travellers : relevantTravellers) {
            for (SchemaDetail row : schemaDetails) {
                boolean sitNextToTraveller = (request.seat() == row.getColumn1() && travellers.getSeat() == row.getColumn2()) || (request.seat() == row.getColumn2() && travellers.getSeat() == row.getColumn1()) || (request.seat() == row.getColumn4() && travellers.getSeat() == row.getColumn5()) || (request.seat() == row.getColumn5() && travellers.getSeat() == row.getColumn4());
                if (sitNextToTraveller) {
                    String existingGender = travellers.getGender().trim().toLowerCase(Locale.ROOT);
                    return requestedGender.equals(existingGender);
                }
            }
        }
        return true;
    }

    private static boolean genderLock(CreateTraveller request, Voyages voyages, List<Travellers> relevantTravellers) {
        List<SchemaDetail> schemaDetails = voyages.getBus().getSchemaHeader().getSchemaDetails();

        int up = 0;
        int down = 0;
        boolean upBreakPoint = false;
        boolean downBreakPoint = false;

        int[] position = new int[2];

        for (int j = 0; j < schemaDetails.size(); j++) {
            if (schemaDetails.get(j).getColumn1() == request.seat()) {
                position[0] = j;
                position[1] = 0;
            }
            if (schemaDetails.get(j).getColumn2() == request.seat()) {
                position[0] = j;
                position[1] = 1;
            }
            if (schemaDetails.get(j).getColumn4() == request.seat()) {
                position[0] = j;
                position[1] = 3;
            }
            if (schemaDetails.get(j).getColumn5() == request.seat()) {
                position[0] = j;
                position[1] = 4;
            }
        }

        for (Travellers traveller : relevantTravellers) {
            String gender = traveller.getGender().trim().toLowerCase(Locale.ROOT);
            for (int l = 0; l < 3; l++) {
                int upperPos = position[0] - (l + 1);
                int lowerPos = position[0] + (l + 1);
                if (position[1] == 0) {
                    if (position[0] - (l + 1) < (position[0] - l) && upperPos >= 0) {
                        if (schemaDetails.get(upperPos).getColumn1() != 0) {
                            if (traveller.getSeat() == schemaDetails.get(upperPos).getColumn1()) {
                                if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                    up++;
                                else upBreakPoint = true;
                            }
                        }

                    }
                    if ((position[0] + l) < position[0] + (l + 1) && lowerPos < schemaDetails.size()) {
                        if (schemaDetails.get(lowerPos).getColumn1() != 0) {
                            if (traveller.getSeat() == schemaDetails.get(lowerPos).getColumn1()) {
                                if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                    down++;
                                else downBreakPoint = true;
                            }
                        }
                    }
                }
                if (position[1] == 1) {
                    if (upperPos >= 0 && schemaDetails.get(upperPos).getColumn2() != 0 && traveller.getSeat() == schemaDetails.get(upperPos).getColumn2()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                            up++;
                        else upBreakPoint = true;
                    } // schemaDetails.get(upperPos).getColumn2() != 0 kondisyonunda flaglenmesi gerekyiyor.
                    if (lowerPos < schemaDetails.size() && schemaDetails.get(lowerPos).getColumn2() != 0 && traveller.getSeat() == schemaDetails.get(lowerPos).getColumn2()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                            down++;
                        else downBreakPoint = true;
                    }
                }
                if (position[1] == 3) {
                    if (upperPos >= 0 && schemaDetails.get(upperPos).getColumn4() != 0 && traveller.getSeat() == schemaDetails.get(upperPos).getColumn4()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                            up++;
                        else upBreakPoint = true;
                    }
                    if (lowerPos < schemaDetails.size() && schemaDetails.get(lowerPos).getColumn4() != 0 && traveller.getSeat() == schemaDetails.get(lowerPos).getColumn4()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                            down++;
                        else downBreakPoint = true;
                    }
                }
                if (position[1] == 4) {
                    if (upperPos >= 0 && schemaDetails.get(upperPos).getColumn5() != 0 && traveller.getSeat() == schemaDetails.get(upperPos).getColumn5()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                            up++;
                        else upBreakPoint = true;
                    }
                    if (lowerPos < schemaDetails.size() && schemaDetails.get(lowerPos).getColumn5() != 0 && traveller.getSeat() == schemaDetails.get(lowerPos).getColumn5()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                            down++;
                        else downBreakPoint = true;
                    }
                }
            }
        }
        return up >= 3 || down >= 3 || (up + down) >= 3;
    }

    private static String requiredText(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return value.trim();
    }
}
