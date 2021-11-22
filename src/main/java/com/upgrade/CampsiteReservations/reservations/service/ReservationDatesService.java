package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.exceptions.PeriodIsNoLongerAvailableException;
import com.upgrade.CampsiteReservations.reservations.model.ReservationDates;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.repository.ReservationDaysRepository;
import com.upgrade.CampsiteReservations.reservations.service.cache.BusyDaysCacheHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationDatesService {

  private ReservationDaysRepository reservationDaysRepository;
  private BusyDaysCacheHandler cacheHandler;

  public ReservationDatesService(ReservationDaysRepository reservationDaysRepository, BusyDaysCacheHandler cacheHandler) {
    this.reservationDaysRepository = reservationDaysRepository;
    this.cacheHandler = cacheHandler;
  }

  /**
   *
   * set the query per month to increase the cache efficiency.
   * @return List of days that has a reservation
   */
  private List<LocalDate> getBusyDaysByMonth(int year, Month month) {
    LocalDate monthStart = LocalDate.of(year, month, 1);
    LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
    return reservationDaysRepository.findAllByDayBetween(monthStart, monthEnd).stream()
        .filter(ReservationDates::isBusy)
        .map(ReservationDates::getDay)
        .collect(Collectors.toList());
  }


  public List<LocalDate> getBusyDays(LocalDate from, LocalDate to) {
    return Stream.iterate(from, date -> date.plusMonths(1))
        .limit(ChronoUnit.MONTHS.between(from, to.plusMonths(1)))
        .flatMap(month -> getBusyDaysByMonth(month.getYear(), month.getMonth()).stream())
        .collect(Collectors.toList());
  }

  public List<ReservationDates> registerAvailability(Reservation reservation) {
    releaseDays(reservation);
    periodIsAvailable(reservation);

    List<ReservationDates> reservations = generateBusyDays(reservation);

    if (reservations.isEmpty()) {
      return new ArrayList<>();
    }
    List<ReservationDates> campsiteAvailabilities = reservationDaysRepository.saveAll(reservations);
    cacheHandler.cacheEvict(reservation);
    return campsiteAvailabilities;
  }

  private List<ReservationDates> generateBusyDays(Reservation reservation) {
    return Stream.iterate(reservation.getArrivalDate(), date -> date.plusDays(1))
        .limit(ChronoUnit.DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate()))
        .map(d -> new ReservationDates(d, reservation))
        .collect(Collectors.toList());
  }

  public void releaseDays(Reservation reservation) {
    reservationDaysRepository.deleteAllByReservation(reservation);
    cacheHandler.cacheEvict(reservation);
  }

  public void periodIsAvailable(Reservation reservation) {
    List<LocalDate> busyDays = getBusyDays(reservation.getArrivalDate(), reservation.getDepartureDate());
    Optional<LocalDate> any = Stream.iterate(reservation.getArrivalDate(), date -> date.plusDays(1))
        .limit(ChronoUnit.DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate().plusDays(1)))
        .filter(busyDays::contains)
        .findAny();
    if(any.isPresent()){
      throw new PeriodIsNoLongerAvailableException(reservation.getArrivalDate(), reservation.getDepartureDate());
    }
  }

  public List<ReservationDates> getDates(LocalDate arrivalDate, LocalDate departureDate) {
    return Stream.iterate(arrivalDate, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(arrivalDate,departureDate))
            .map(d -> new ReservationDates(d))
            .collect(Collectors.toList());
  }
}
