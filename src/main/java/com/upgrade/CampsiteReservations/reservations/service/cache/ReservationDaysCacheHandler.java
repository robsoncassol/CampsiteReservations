package com.upgrade.CampsiteReservations.reservations.service.cache;

import com.upgrade.CampsiteReservations.config.RedisCacheConfig;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationDaysCacheHandler {

  private CacheManager cacheManager;

  public ReservationDaysCacheHandler(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public void cacheEvict(Reservation reservation) {
    Cache busyDaysByMonthCache = cacheManager.getCache(RedisCacheConfig.BUSY_DAYS_BY_MONTH);
    if(busyDaysByMonthCache!=null) {
      List<LocalDate> affectedMonths = getAffectedMonths(reservation);
      affectedMonths.forEach(month -> busyDaysByMonthCache.evictIfPresent(getKey(month)));
    }
  }

  public void cacheEvict() {
    Cache busyDaysByMonthCache = cacheManager.getCache(RedisCacheConfig.BUSY_DAYS_BY_MONTH);
    if(busyDaysByMonthCache!=null) {
      busyDaysByMonthCache.clear();
    }
  }

  private Object getKey(LocalDate month) {
    return SimpleKeyGenerator.generateKey(month.withDayOfMonth(1), month.withDayOfMonth(month.lengthOfMonth()));
  }

  private List<LocalDate> getAffectedMonths(Reservation reservation) {
    return Stream.iterate(reservation.getArrivalDate(), date -> date.plusMonths(1))
        .limit(ChronoUnit.MONTHS.between(reservation.getArrivalDate(), reservation.getDepartureDate().plusMonths(1)))
        .collect(Collectors.toList());
  }

}
