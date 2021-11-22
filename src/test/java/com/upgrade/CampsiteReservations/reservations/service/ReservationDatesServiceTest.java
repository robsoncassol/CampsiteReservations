package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.exceptions.PeriodIsNoLongerAvailableException;
import com.upgrade.CampsiteReservations.reservations.model.ReservationDates;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.repository.ReservationDaysRepository;
import com.upgrade.CampsiteReservations.reservations.service.cache.BusyDaysCacheHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationDatesServiceTest {

  @InjectMocks
  private ReservationDatesService reservationDatesService;

  @Captor
  private ArgumentCaptor<List<ReservationDates>> captor;

  @Mock
  private ReservationDaysRepository reservationDaysRepository;

  @Mock
  private BusyDaysCacheHandler cacheHandler;

  @BeforeEach
  void setup(){

  }

  @Test
  void testWhenPeriodIsNotAvailableShouldThrowException() {
    LocalDate october = LocalDate.of(2021, 10, 1);
    //All days are busy
    Mockito.when(reservationDaysRepository.findAllByDayBetween(october,october.withDayOfMonth(october.lengthOfMonth()))).thenReturn(getCampsiteAvailabilityForTheWholeMonth(october));
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(october.withDayOfMonth(10));
    reservation.setDepartureDate(october.withDayOfMonth(12));
    Assertions.assertThrows(PeriodIsNoLongerAvailableException.class,()-> reservationDatesService.periodIsAvailable(reservation));
  }

  @NotNull
  private List<ReservationDates> getCampsiteAvailabilityForTheWholeMonth(LocalDate month) {
    Reservation reservation = new Reservation();
    return Stream.iterate(month.withDayOfMonth(1), date -> date.plusDays(1))
        .limit(month.lengthOfMonth())
        .map(d -> new ReservationDates(d, reservation))
        .collect(Collectors.toList());
  }

  @Test
  void testWhenPeriodIsAvailableShouldNotThrowException() {
    //All days are available
    Mockito.when(reservationDaysRepository.findAllByDayBetween(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.now());
    reservation.setDepartureDate(LocalDate.now().plusDays(1));
    reservationDatesService.periodIsAvailable(reservation);
  }

  @Test
  void testCreateAndSave() {
    Reservation reservation = new Reservation();
    reservation.setArrivalDate(LocalDate.of(2021, 11, 10));
    reservation.setDepartureDate(LocalDate.of(2021, 11, 20));
    reservationDatesService.registerAvailability(reservation);
    verify(reservationDaysRepository).saveAll(captor.capture());
    Assertions.assertNotNull(captor.getValue());
    Assertions.assertEquals(10, captor.getValue().size());
  }

}