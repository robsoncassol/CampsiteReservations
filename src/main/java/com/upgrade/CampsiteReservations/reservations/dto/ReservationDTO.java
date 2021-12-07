package com.upgrade.CampsiteReservations.reservations.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationDTO {

  @ApiModelProperty(
      notes = "The unique identifier for the reservation"
  )
  private Long id;

  @NotEmpty(message = "Name can't be empty")
  @ApiModelProperty(
      notes = "The client name"
  )
  private String name;

  @NotEmpty(message = "Email can't be empty")
  @Email(message = "must be a well-formed email address")
  @ApiModelProperty(
      notes = "The email where the reservation will be sent"
  )
  private String email;

  @NotNull(message = "Arrival date can't be null")
  @ApiModelProperty(
      notes = "Arrival date, must be before departure date"
  )
  private LocalDate arrivalDate;

  @NotNull(message = "Departure date can't be null")
  @ApiModelProperty(
      notes = "Departure date, must be after arrival date"
  )
  private LocalDate departureDate;

}
