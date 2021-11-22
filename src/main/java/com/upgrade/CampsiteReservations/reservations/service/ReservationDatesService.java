package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.exceptions.PeriodIsNoLongerAvailableException;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.model.ReservationDate;
import com.upgrade.CampsiteReservations.reservations.repository.ReservationDaysRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationDatesService {

  private ReservationDaysRepository reservationDaysRepository;


  public ReservationDatesService(ReservationDaysRepository reservationDaysRepository) {
    this.reservationDaysRepository = reservationDaysRepository;
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
        .filter(ReservationDate::isBusy)
        .map(ReservationDate::getDay)
        .collect(Collectors.toList());
  }

  public List<LocalDate> getBusyDays(LocalDate from, LocalDate to) {
    return Stream.iterate(from, date -> date.plusMonths(1))
        .limit(ChronoUnit.MONTHS.between(from, to.plusMonths(1)))
        .flatMap(month -> getBusyDaysByMonth(month.getYear(), month.getMonth()).stream())
        .collect(Collectors.toList());
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


}
