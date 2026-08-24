package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.*;
import com.gokmen.otobusapi.repository.entities.*;
import com.gokmen.otobusapi.service.TravellersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TravellersServiceImpl implements TravellersService {

    private final TravellerRepository travellerRepository;
    private final BussRepository bussRepository;
    private final VoyagesRepository voyagesRepository;


    public TravellersServiceImpl(TravellerRepository travellerRepository, BussRepository bussRepository, VoyagesRepository voyagesRepository) {
        this.travellerRepository = travellerRepository;
        this.bussRepository = bussRepository;
        this.voyagesRepository = voyagesRepository;
    }


    @Override
    public void setTraveller(String voyageId, int busNo, int seatNo, boolean foreign, Travellers travellers) {
        voyagesRepository.findByNo(voyageId).ifPresent(voyages -> {
            if (bussRepository.findById(busNo).stream().allMatch(buss -> buss.getMax_traveller() >= seatNo && seatNo >0)) {
                if (!foreign) {

                    if (travellers.getIndentityNumber() != null || travellers.getIndentityNumber().length() == 11) {
                        if (validId(travellers)) {

                            String first;
                            String midlle;
                            String last;

                            first = travellers.getIndentityNumber().substring(0, 2);
                            midlle = "*******";
                            last = travellers.getIndentityNumber().substring(9);

                            boolean samevoyage;

                            travellers.setFirstStation(voyages.getFirstStation());
                            travellers.setLastStation(voyages.getLastStation());

                            if (!travellerRepository.findAll().isEmpty()) {
                                travellerRepository.findAll().forEach(travellers1 -> {
                                    //if (travellers1.getVoyageNo() != travellers.getVoyageNo())
                                    if (travellers1.getSeat() == seatNo && ((travellers.getLastStation() <= travellers1.getFirstStation() && travellers.getFirstStation() < travellers1.getFirstStation()) || (travellers1.getLastStation() <= travellers.getFirstStation() && travellers1.getLastStation() < travellers.getLastStation()))) { // Aynı sefer girilince Kaydediyor
                                        travellers.setBus_id(bussRepository.findById(busNo).stream().toList());
                                        //travellers.setBus_id(voyages.getRoutes().getBuss());
                                        travellers.setSeat(seatNo);
                                        travellers.setVoyageNo(voyages);
                                        travellers.setIndentityNumber(first + midlle + last);
                                        travellers.setTravelStart(voyages.getStartDate());
                                        travellers.setTravelEnd(voyages.getEndDate());
                                        travellerRepository.save(travellers);
                                    } else if (travellers1.getSeat() != seatNo) {
                                        travellers.setBus_id(bussRepository.findById(busNo).stream().toList());
                                        //travellers.setBus_id(voyages.getRoutes().getBuss());
                                        travellers.setSeat(seatNo);
                                        travellers.setVoyageNo(voyages);
                                        travellers.setIndentityNumber(first + midlle + last);
                                        travellers.setTravelStart(voyages.getStartDate());
                                        travellers.setTravelEnd(voyages.getEndDate());
                                        travellerRepository.save(travellers);
                                    } else System.out.println("Unavailable seat");
                                });
                            } else {
                                travellers.setBus_id(bussRepository.findById(busNo).stream().toList());
                                //travellers.setBus_id(voyages.getRoutes().getBuss());
                                travellers.setSeat(seatNo);
                                travellers.setVoyageNo(voyages);
                                travellers.setIndentityNumber(first + midlle + last);
                                travellers.setTravelStart(voyages.getStartDate());
                                travellers.setTravelEnd(voyages.getEndDate());
                                travellerRepository.save(travellers);                                                       //Reflect hatası creat-drop at
                            }
                        } else {
                            System.out.println("Invalid id");
                        }
                    }
                } else {
                    if (!travellerRepository.findAll().isEmpty()) {
                        travellerRepository.findAll().forEach(travellers1 -> {
                            if (travellers1.getSeat() == seatNo && ((travellers.getLastStation() <= travellers1.getFirstStation() && travellers.getFirstStation() < travellers1.getFirstStation()) || (travellers1.getLastStation() <= travellers.getFirstStation() && travellers1.getLastStation() < travellers.getLastStation()))) {
                                travellers.setBus_id(bussRepository.findById(busNo).stream().toList());
                                //travellers.setBus_id(voyages.getRoutes().getBuss());
                                //travellers.setBus_id(bussRepository.findById(busNo).stream().toList());
                                travellers.setForeign(true);
                                travellers.setSeat(seatNo);
                                travellers.setVoyageNo(voyages);
                                travellers.setIndentityNumber("Foreigner");
                                travellers.setTravelStart(voyages.getStartDate());
                                travellers.setTravelEnd(voyages.getEndDate());
                                travellerRepository.save(travellers);
                            } else if (travellers1.getSeat() != seatNo) {
                                travellers.setBus_id(bussRepository.findById(busNo).stream().toList());
                                //travellers.setBus_id(voyages.getRoutes().getBuss());
                                travellers.setForeign(true);
                                travellers.setSeat(seatNo);
                                travellers.setVoyageNo(voyages);
                                travellers.setIndentityNumber("Foreigner");
                                travellers.setTravelStart(voyages.getStartDate());
                                travellers.setTravelEnd(voyages.getEndDate());
                                travellerRepository.save(travellers);
                            } else System.out.println("Unavailable seat");
                        });
                    } else {
                        travellers.setBus_id(bussRepository.findById(busNo).stream().toList());
                        //travellers.setBus_id(voyages.getRoutes().getBuss());
                        travellers.setForeign(true);
                        travellers.setSeat(seatNo);
                        travellers.setVoyageNo(voyages);
                        travellers.setIndentityNumber("Foreigner");
                        travellers.setTravelStart(voyages.getStartDate());
                        travellers.setTravelEnd(voyages.getEndDate());
                        travellerRepository.save(travellers);
                    }
                }
            }
        });
    }

    private boolean validId(Travellers traveller) {
        int[] numbers = new int[11];

        for (int i = 0; i < 11; i++) {
            numbers[i] = Integer.parseInt(traveller.getIndentityNumber().substring(i, (i + 1)));
        }
        boolean condition1 = (numbers[0] + numbers[1] + numbers[2] + numbers[3] + numbers[4] + numbers[5] + numbers[6] + numbers[7] + numbers[8] + numbers[9]) % 10 == numbers[10];
        boolean condition2 = (((numbers[0] + numbers[2] + numbers[4] + numbers[6] + numbers[8]) * 7) + ((numbers[1] + numbers[3] + numbers[5] + numbers[7]) * 9)) % 10 == numbers[9];
        boolean condition3 = ((numbers[0] + numbers[2] + numbers[4] + numbers[6] + numbers[8]) * 8) % 10 == numbers[10];
        if (condition1 && condition2 && condition3) return true;
        else return false;
    }

    @Override
    public void updateTraveller(int travelerId, Travellers travellers) {
        travellerRepository.findById(travelerId).ifPresent(travellers1 -> {
            travellers1.setTravellerName(travellers.getTravellerName());
            travellers1.setTravellerSurname(travellers.getTravellerSurname());
            travellers1.setGender(travellers.getGender());
            travellerRepository.save(travellers1);
        });
    }

    @Override
    public List<Travellers> findAllTravellers() {
        return travellerRepository.findAll();
    }

    @Override
    public Optional<Travellers> findById(int travellerId) {
        if (travellerRepository.findById(travellerId).isPresent())
            return travellerRepository.findById(travellerId);
        else
            return Optional.empty();
    }

    @Override
    public Optional<Travellers> findByVoyage(String voyageNo) {
        if (travellerRepository.findAllByVoyage(voyageNo).isPresent())
            return travellerRepository.findAllByVoyage(voyageNo);
        else
            return Optional.empty();
    }

    @Override
    public Optional<Travellers> findByBus(String plateNumber) {
        if (bussRepository.findAllTravellersByPlateNumber(plateNumber).isPresent())
            return bussRepository.findAllTravellersByPlateNumber(plateNumber);
        else
            return Optional.empty();
    }
}

