package com.upgrade.CampsiteReservations.reservations.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ReservationDate> reservationDates = new HashSet<>();

  public void addReservationDates(List<ReservationDate> dates) {
    dates.forEach(d -> d.setReservation(this));
    reservationDates.addAll(dates);
  }

  @Override
  public String toString() {
    return "Reservation{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", arrivalDate=" + arrivalDate +
        ", departureDate=" + departureDate +
        '}';
  }

}
