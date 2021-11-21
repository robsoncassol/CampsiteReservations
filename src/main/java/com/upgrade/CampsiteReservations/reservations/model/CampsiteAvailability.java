package com.upgrade.CampsiteReservations.reservations.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class CampsiteAvailability implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "reservation_id")
  private Reservation reservation;

  @Column(unique = true)
  private LocalDate day;

  public boolean isBusy() {
    return reservation != null;
  }

  public CampsiteAvailability(LocalDate day, Reservation reservation) {
    this.reservation = reservation;
    this.day = day;
  }

  public LocalDate getDay() {
    return day;
  }
}
