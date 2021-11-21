package com.upgrade.CampsiteReservations.reservations.repository;

import com.upgrade.CampsiteReservations.config.RedisCacheConfig;
import com.upgrade.CampsiteReservations.reservations.model.CampsiteAvailability;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampsiteAvailabilityRepository extends JpaRepository<CampsiteAvailability, Long> {


  @Cacheable(value = RedisCacheConfig.BUSY_DAYS_BY_MONTH, key = "new org.springframework.cache.interceptor.SimpleKey(#monthStart, #monthEnd)")
  List<CampsiteAvailability> findAllByDayBetween(LocalDate monthStart, LocalDate monthEnd);

  void deleteAllByReservation(Reservation reservation);
}
