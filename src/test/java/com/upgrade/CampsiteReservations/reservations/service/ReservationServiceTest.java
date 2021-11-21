package com.upgrade.CampsiteReservations.reservations.service;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock
  private CampsiteAvailabilityService campsiteAvailabilityService;

  @Mock
  private ReservationValidator reservationValidator;

  @InjectMocks
  private ReservationService reservationService;


  @Test
  void testGetAvailableDatesWithAllDaysAvailable() {
    LocalDate from = LocalDate.of(2021, Month.MAY, 10);
    LocalDate to = LocalDate.of(2021, Month.MAY, 20);
    List<LocalDate> availableDates = reservationService.getAvailableDates(from, to);
    Assertions.assertEquals(11, availableDates.size());
  }

  @Test
  void testGetAvailableDatesWithTwoBusyDays() {
    List<LocalDate> busyDays = Lists.list(LocalDate.of(2021, Month.OCTOBER, 11),
        LocalDate.of(2021, Month.OCTOBER, 18));
    Mockito.when(campsiteAvailabilityService.getBusyDays(Mockito.any(), Mockito.any())).thenReturn(busyDays);
    LocalDate from = LocalDate.of(2021, Month.OCTOBER, 10);
    LocalDate to = LocalDate.of(2021, Month.OCTOBER, 20);
    List<LocalDate> availableDates = reservationService.getAvailableDates(from, to);
    Assertions.assertEquals(9, availableDates.size());
  }

  @Test
  void testGetAvailableDatesOverTheYear() {
    List<LocalDate> busyDays = Lists.list(LocalDate.of(2021, Month.DECEMBER, 11),
        LocalDate.of(2021, Month.DECEMBER, 18));
    LocalDate from = LocalDate.of(2021, Month.DECEMBER, 10);
    LocalDate to = LocalDate.of(2022, Month.JANUARY, 10);
    Mockito.when(campsiteAvailabilityService.getBusyDays(from, to))
        .thenReturn(busyDays);

    List<LocalDate> availableDates = reservationService.getAvailableDates(from, to);
    Assertions.assertEquals(30, availableDates.size());
  }

}