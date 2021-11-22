package com.upgrade.CampsiteReservations.reservations.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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

  @OneToMany(mappedBy = "reservation", orphanRemoval = true)
  private List<ReservationDates> reservationDates;

  public void addReservationDates(List<ReservationDates> dates) {
    dates.forEach(d -> d.setReservation(this));
    reservationDates.addAll(dates);
  }
}
