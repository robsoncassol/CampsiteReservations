package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.exceptions.InconsistentReservationDatesException;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
public class ReservationValidator {

  private int maxAllowedPeriodInDays;

  public ReservationValidator(@Value("${campsite.reservation.max-period}") int maxAllowedPeriodInDays) {
    this.maxAllowedPeriodInDays = maxAllowedPeriodInDays;
  }

  public void validated(Reservation reservation) {
    if(reservation.getArrivalDate().isAfter(reservation.getDepartureDate())){
      throw new InconsistentReservationDatesException("Arrival date is after departure date");
    }

    if(reservation.getDepartureDate().isAfter(reservation.getArrivalDate().plusDays(maxAllowedPeriodInDays))){
      throw new InconsistentReservationDatesException(String.format("The maximum allowed period is %d days",maxAllowedPeriodInDays));
    }

    if(reservation.getDepartureDate().isEqual(reservation.getArrivalDate())){
      throw new InconsistentReservationDatesException("The arrival date is the same as the departure date");
    }

  }

}
