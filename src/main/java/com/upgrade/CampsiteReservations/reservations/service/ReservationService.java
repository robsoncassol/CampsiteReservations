package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.LocalDateUtil;
import com.upgrade.CampsiteReservations.reservations.dto.AvailableDateDTO;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.model.ReservationDate;
import com.upgrade.CampsiteReservations.reservations.repository.ReservationRepository;
import com.upgrade.CampsiteReservations.reservations.service.cache.ReservationDaysCacheHandler;
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
  private ReservationDaysCacheHandler cacheHandler;

  ReservationService(ReservationRepository reservationRepository,
                     ReservationDatesService reservationDatesService,
                     ReservationValidator reservationValidator,
                     ReservationDaysCacheHandler cacheHandler) {
    this.reservationRepository = reservationRepository;
    this.reservationDatesService = reservationDatesService;
    this.reservationValidator = reservationValidator;
    this.cacheHandler = cacheHandler;
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
    reservationDatesService.periodIsAvailable(reservation);
    reservation.addReservationDates(generateDates(reservation));
    Reservation savedReservation = reservationRepository.save(reservation);
    cacheHandler.cacheEvict(reservation);
    return savedReservation;
  }


  @Transactional
  public Reservation updateReservation(Long id, Reservation reservation) {
    reservationValidator.validated(reservation);
    Reservation reservationDb = reservationRepository.getById(id);
    reservationDatesService.periodIsAvailable(reservation, reservationDb.getReservationDates());
    reservationDb.setArrivalDate(reservation.getArrivalDate());
    reservationDb.setDepartureDate(reservation.getDepartureDate());
    reservationDb.setName(reservation.getName());
    reservationDb.setEmail(reservation.getEmail());
    reservationDb.getReservationDates().clear();
    reservationDb.addReservationDates(generateDates(reservationDb));
    cacheHandler.cacheEvict(reservation);
    return reservationRepository.save(reservationDb);
  }

  public List<ReservationDate> generateDates(Reservation reservation) {
    return LocalDateUtil.getDaysBetweenDates(reservation)
        .stream()
        .map(d -> new ReservationDate(d,reservation))
        .collect(Collectors.toList());
  }

  public Optional<Reservation> getReservationById(Long id) {
    return reservationRepository.findById(id);
  }

  public void cancelReservation(Reservation reservation) {
    reservationRepository.delete(reservation);
    cacheHandler.cacheEvict(reservation);
  }
}
