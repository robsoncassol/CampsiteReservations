package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationService {

  private ReservationRepository reservationRepository;
  private CampsiteAvailabilityService campsiteAvailabilityService;

  ReservationService(ReservationRepository reservationRepository,
                     CampsiteAvailabilityService campsiteAvailability) {
    this.reservationRepository = reservationRepository;
    this.campsiteAvailabilityService = campsiteAvailability;
  }

  public List<LocalDate> getAvailableDates(LocalDate from, LocalDate to) {
    List<LocalDate> busyDays = campsiteAvailabilityService.getBusyDays(from, to);

    return Stream.iterate(from, date -> date.plusDays(1))
        .limit(ChronoUnit.DAYS.between(from, to.plusDays(1)))
        .filter(d -> !busyDays.contains(d))
        .collect(Collectors.toList());
  }


  @Transactional
  public Reservation bookCampsite(Reservation reservation) {
    Reservation savedReservation = reservationRepository.save(reservation);
    campsiteAvailabilityService.createAndSave(savedReservation);
    return savedReservation;
  }

  @Transactional
  public Reservation updateReservation(Long id, Reservation reservation) {
    reservation.setId(id);
    campsiteAvailabilityService.releaseItFor(reservation);
    Reservation savedReservation = reservationRepository.save(reservation);
    campsiteAvailabilityService.createAndSave(savedReservation);
    return savedReservation;
  }

  public Optional<Reservation> getReservationById(Long id) {
    return reservationRepository.findById(id);
  }

  public Boolean cancelReservation(Reservation reservation) {
    return true;
  }
}
