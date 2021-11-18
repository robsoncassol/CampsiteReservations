package com.upgrade.CampsiteReservations.reservations.exceptions;

public class InconsistentReservationDatesException extends RuntimeException {

  private String errorDetail;

  public InconsistentReservationDatesException(String errorDetail) {
    super("The selected period is not valid");
    this.errorDetail = errorDetail;
  }

  public String getErrorDetails() {
    return errorDetail;
  }


}
