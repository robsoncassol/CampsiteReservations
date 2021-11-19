package com.upgrade.CampsiteReservations.reservations.exceptions;

public class BusinessValidationException extends RuntimeException {

  private String errorDetail;

  public BusinessValidationException(String errorDetail) {
    super("The selected period is not valid");
    this.errorDetail = errorDetail;
  }

  public String getErrorDetails() {
    return errorDetail;
  }

}
