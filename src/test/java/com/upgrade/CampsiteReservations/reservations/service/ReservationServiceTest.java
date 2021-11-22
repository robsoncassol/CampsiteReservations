package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.dto.AvailableDateDTO;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock
  private ReservationDatesService reservationDatesService;

  @Mock
  private ReservationValidator reservationValidator;

  @InjectMocks
  private ReservationService reservationService;


  @Test
  void testGetAvailableDatesWithAllDaysAvailable() {
    LocalDate from = LocalDate.of(2021, Month.MAY, 10);
    LocalDate to = LocalDate.of(2021, Month.MAY, 20);
    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(from, to);
    Assertions.assertEquals(11, availableDates.size());
  }

  @Test
  void testGetAvailableDatesWithTwoBusyDays() {
    List<LocalDate> busyDays = Lists.list(LocalDate.of(2021, Month.OCTOBER, 11),
        LocalDate.of(2021, Month.OCTOBER, 18));
    Mockito.when(reservationDatesService.getBusyDays(Mockito.any(), Mockito.any())).thenReturn(busyDays);
    LocalDate from = LocalDate.of(2021, Month.OCTOBER, 10);
    LocalDate to = LocalDate.of(2021, Month.OCTOBER, 20);
    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(from, to);
    Assertions.assertEquals(9, availableDates.stream().filter(AvailableDateDTO::isAvailable).count());
  }

  @Test
  void testGetAvailableDatesOverTheYear() {
    List<LocalDate> busyDays = Lists.list(LocalDate.of(2021, Month.DECEMBER, 11),
        LocalDate.of(2021, Month.DECEMBER, 18));
    LocalDate from = LocalDate.of(2021, Month.DECEMBER, 10);
    LocalDate to = LocalDate.of(2022, Month.JANUARY, 10);
    Mockito.when(reservationDatesService.getBusyDays(from, to))
        .thenReturn(busyDays);

    List<AvailableDateDTO> availableDates = reservationService.getAvailableDates(from, to);
    Assertions.assertEquals(30, availableDates.stream().filter(AvailableDateDTO::isAvailable).count());
  }

}