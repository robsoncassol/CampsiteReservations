package com.upgrade.CampsiteReservations.reservations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDateDTO {

  private LocalDate date;
  private boolean available;

}
