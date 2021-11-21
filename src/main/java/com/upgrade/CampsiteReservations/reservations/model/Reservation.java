package com.upgrade.CampsiteReservations.reservations.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Setter
@Getter
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
