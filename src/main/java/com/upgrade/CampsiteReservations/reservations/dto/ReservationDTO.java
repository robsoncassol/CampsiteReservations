package com.upgrade.CampsiteReservations.reservations.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ReservationDTO {

  private Long id;

  @NotEmpty
  private String name;

  @NotEmpty
  @Email
  private String email;

  @NotNull
  private LocalDate arrivalDate;

  @NotNull
  private LocalDate departureDate;

}
