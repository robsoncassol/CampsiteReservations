package com.upgrade.CampsiteReservations.reservations.exceptions;

import java.time.LocalDate;

public class PeriodIsNoLongerAvailableException extends  BusinessValidationException{


  public PeriodIsNoLongerAvailableException(LocalDate arrival, LocalDate departure) {
    super(String.format("Selected period is no longer available [%s - %s]",arrival,departure));
  }
}
