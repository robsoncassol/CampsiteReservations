package com.upgrade.CampsiteReservations.reservations.service;

import com.upgrade.CampsiteReservations.reservations.exceptions.InvalidPeriodException;
import com.upgrade.CampsiteReservations.reservations.exceptions.InvalidSearchPeriodException;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Service
@Log4j2
public class ReservationValidator {

  private int maxAllowedPeriodInDays;
  private int maxAllowedSearchPeriod;

  public ReservationValidator(@Value("${campsite.reservation.max-period}") int maxAllowedPeriodInDays,
                              @Value("${campsite.search.max-period}") int maxAllowedSearchPeriod
  ) {
    this.maxAllowedPeriodInDays = maxAllowedPeriodInDays;
    this.maxAllowedSearchPeriod = maxAllowedSearchPeriod;
  }

  public boolean validated(Reservation reservation) {
    log.info("Validating reservation {}", reservation);
    Period period = Period.between(reservation.getArrivalDate(), reservation.getDepartureDate());
    if(period.isNegative()){
      throw new InvalidPeriodException("Arrival date is after departure date");
    }

    if(ChronoUnit.DAYS.between(reservation.getArrivalDate(),reservation.getDepartureDate()) > maxAllowedPeriodInDays){
      throw new InvalidPeriodException(String.format("The booking period cannot be longer than %d days",maxAllowedPeriodInDays));
    }

    if(period.isZero()){
      throw new InvalidPeriodException("The arrival date is the same as the departure date");
    }

    LocalDate arrivalDate = reservation.getArrivalDate();
    LocalDate now = LocalDate.now();
    if(arrivalDate.isBefore(now.plusDays(1))){
      throw new InvalidPeriodException("The arrival date should be minimum 1 day(s) ahead");
    }

    if(arrivalDate.isAfter(now.plusMonths(1))){
      throw new InvalidPeriodException("The arrival date should be a maximum of up to 1 month in advance");
    }

    return true;
  }

  public void validateAvailableDatesRange(LocalDate from, LocalDate to) {
    log.info("Validating search period from:{} util:{}", from, to);
    Period period = Period.between(from, to);
    if(period.isNegative()){
      throw new InvalidSearchPeriodException("The final date is after the initial date");
    }

    if(ChronoUnit.DAYS.between(from,to) > maxAllowedSearchPeriod){
      throw new InvalidSearchPeriodException(String.format("The searching period cannot be longer than %d days",maxAllowedSearchPeriod));
    }

    if(period.isZero()){
      throw new InvalidSearchPeriodException("The initial date is the same as the end date");
    }


  }
}
