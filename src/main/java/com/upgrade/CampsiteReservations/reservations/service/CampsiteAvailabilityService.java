package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.config.RedisCacheConfig;
import com.upgrade.CampsiteReservations.reservations.model.CampsiteAvailability;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.repository.CampsiteAvailabilityRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CampsiteAvailabilityService {

  private CampsiteAvailabilityRepository campsiteAvailabilityRepository;
  private CacheManager cacheManager;

  public CampsiteAvailabilityService(CampsiteAvailabilityRepository campsiteAvailabilityRepository, CacheManager cacheManager) {
    this.campsiteAvailabilityRepository = campsiteAvailabilityRepository;
    this.cacheManager = cacheManager;
  }

  public List<LocalDate> getBusyDaysByMonth(int year, Month month) {
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
    cacheEvict(reservation);
    return campsiteAvailabilities;
  }

  private void cacheEvict(Reservation reservation) {
    Cache busyDaysByMonthCache = cacheManager.getCache(RedisCacheConfig.BUSY_DAYS_BY_MONTH);
    if(busyDaysByMonthCache!=null) {
      busyDaysByMonthCache.clear();
    }
  }

  private List<CampsiteAvailability> generateBusyDays(Reservation reservation) {
    List<CampsiteAvailability> reservations = Stream.iterate(reservation.getArrivalDate(), date -> date.plusDays(1))
        .limit(ChronoUnit.DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate()))
        .map(d -> new CampsiteAvailability(d, reservation))
        .collect(Collectors.toList());
    return reservations;
  }

  public void releaseItFor(Reservation reservation) {
    campsiteAvailabilityRepository.deleteAllByReservation(reservation);
  }
}
