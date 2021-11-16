package com.upgrade.CampsiteReservations.reservations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

  private Long id;

  @NotEmpty(message = "Name can't be empty")
  private String name;

  @NotEmpty(message = "Email can't be empty")
  @Email
  private String email;

  @NotNull(message = "Arrival date can't be null")
  private LocalDate arrivalDate;

  @NotNull(message = "Departure date can't be null")
  private LocalDate departureDate;

}
