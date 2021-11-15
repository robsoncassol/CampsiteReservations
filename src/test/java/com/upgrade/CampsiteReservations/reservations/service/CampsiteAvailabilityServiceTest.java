package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.model.CampsiteAvailability;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.repository.CampsiteAvailabilityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CampsiteAvailabilityServiceTest {

  @InjectMocks
  private CampsiteAvailabilityService campsiteAvailabilityService;

  @Captor
  private ArgumentCaptor<List<CampsiteAvailability>> captor;

  @Mock
  private CampsiteAvailabilityRepository campsiteAvailabilityRepository;

  @Mock
  private CacheManager cacheManager;

  @BeforeEach
  void setup(){

  }

  @Test
  void getBusyDaysByMonth() {
  }

  @Test
  void getBusyDays() {
  }


  @Test
  void testCreateAndSave() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.of(2021, 11, 10));
    reservation.setDepartureDate(LocalDate.of(2021, 11, 20));
    campsiteAvailabilityService.createAndSave(reservation);
    verify(campsiteAvailabilityRepository).saveAll(captor.capture());
    Assertions.assertNotNull(captor.getValue());
    Assertions.assertEquals(10, captor.getValue().size());
  }

  @Test
  void testUpdateCampsiteAvailability() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.of(2021, 11, 10));
    reservation.setDepartureDate(LocalDate.of(2021, 11, 20));
    campsiteAvailabilityService.createAndSave(reservation);
    verify(campsiteAvailabilityRepository).saveAll(captor.capture());
    Assertions.assertNotNull(captor.getValue());
    Assertions.assertEquals(10, captor.getValue().size());
  }
}