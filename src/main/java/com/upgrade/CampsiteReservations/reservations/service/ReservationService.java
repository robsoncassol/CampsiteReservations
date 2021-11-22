package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.dto.AvailableDateDTO;
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
  private ReservationDatesService reservationDatesService;
  private ReservationValidator reservationValidator;

  ReservationService(ReservationRepository reservationRepository,
                     ReservationDatesService campsiteAvailability,
                     ReservationValidator reservationValidator) {
    this.reservationRepository = reservationRepository;
    this.reservationDatesService = campsiteAvailability;
    this.reservationValidator = reservationValidator;
  }

  public List<AvailableDateDTO> getAvailableDates(LocalDate from, LocalDate to) {
    reservationValidator.validateAvailableDatesRange(from,to);
    List<LocalDate> busyDays = reservationDatesService.getBusyDays(from, to);

    return Stream.iterate(from, date -> date.plusDays(1))
        .limit(ChronoUnit.DAYS.between(from, to.plusDays(1)))
        .map(d -> new AvailableDateDTO(d, !busyDays.contains(d)))
        .collect(Collectors.toList());
  }


  @Transactional
  public Reservation bookCampsite(Reservation reservation) {
    reservationValidator.validated(reservation);
    Reservation savedReservation = reservationRepository.save(reservation);
    reservationDatesService.registerAvailability(savedReservation);
    return savedReservation;
  }


  @Transactional
  public Reservation updateReservation(Long id, Reservation reservation) {
    reservation.setId(id);
    reservationValidator.validated(reservation);
    reservation.getReservationDates().clear();
    reservationDatesService.periodIsAvailable(reservation);
    reservation.addReservationDates(reservationDatesService.getDates(reservation.getArrivalDate(), reservation.getDepartureDate()));
    return reservationRepository.save(reservation);
  }

  public Optional<Reservation> getReservationById(Long id) {
    return reservationRepository.findById(id);
  }

  @Transactional
  public void cancelReservation(Reservation reservation) {
    reservationDatesService.releaseDays(reservation);
    reservationRepository.delete(reservation);
  }
}
