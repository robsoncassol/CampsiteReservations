package com.upgrade.CampsiteReservations.reservations.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class ReservationDates implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER,optional = false)
  @JoinColumn(name = "reservation_id")
  private Reservation reservation;

  @Column(unique = true)
  private LocalDate day;

  public boolean isBusy() {
    return reservation != null;
  }

  public ReservationDates(LocalDate day, Reservation reservation) {
    this.reservation = reservation;
    this.day = day;
  }

  public ReservationDates(LocalDate day) {
    this.reservation = reservation;
    this.day = day;
  }

  public LocalDate getDay() {
    return day;
  }
}
