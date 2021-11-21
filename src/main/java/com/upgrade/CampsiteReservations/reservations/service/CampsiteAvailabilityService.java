package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.exceptions.PeriodIsNoLongerAvailableException;
import com.upgrade.CampsiteReservations.reservations.model.CampsiteAvailability;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.repository.CampsiteAvailabilityRepository;
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
public class CampsiteAvailabilityService {

  private CampsiteAvailabilityRepository campsiteAvailabilityRepository;
  private BusyDaysCacheHandler cacheHandler;

  public CampsiteAvailabilityService(CampsiteAvailabilityRepository campsiteAvailabilityRepository, BusyDaysCacheHandler cacheHandler) {
    this.campsiteAvailabilityRepository = campsiteAvailabilityRepository;
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
    return campsiteAvailabilityRepository.findAllByDayBetween(monthStart, monthEnd).stream()
        .filter(CampsiteAvailability::isBusy)
        .map(CampsiteAvailability::getDay)
        .collect(Collectors.toList());
  }


  public List<LocalDate> getBusyDays(LocalDate from, LocalDate to) {
    return Stream.iterate(from, date -> date.plusMonths(1))
        .limit(ChronoUnit.MONTHS.between(from, to.plusMonths(1)))
        .flatMap(month -> getBusyDaysByMonth(month.getYear(), month.getMonth()).stream())
        .collect(Collectors.toList());
  }

  public List<CampsiteAvailability> createAndSave(Reservation reservation) {
    List<CampsiteAvailability> reservations = generateBusyDays(reservation);

    if (reservations.isEmpty()) {
      return new ArrayList<>();
    }
    List<CampsiteAvailability> campsiteAvailabilities = campsiteAvailabilityRepository.saveAll(reservations);
    cacheHandler.cacheEvict(reservation);
    return campsiteAvailabilities;
  }

  private List<CampsiteAvailability> generateBusyDays(Reservation reservation) {
    return Stream.iterate(reservation.getArrivalDate(), date -> date.plusDays(1))
        .limit(ChronoUnit.DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate()))
        .map(d -> new CampsiteAvailability(d, reservation))
        .collect(Collectors.toList());
  }

  public void releaseItFor(Reservation reservation) {
    campsiteAvailabilityRepository.deleteAllByReservation(reservation);
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
