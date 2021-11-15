package com.upgrade.CampsiteReservations.reservations.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
public class Reservation implements Serializable {

  @Id
  @GeneratedValue
  private Long id;
  private String name;
  private String email;
  private LocalDate arrivalDate;
  private LocalDate departureDate;


}
