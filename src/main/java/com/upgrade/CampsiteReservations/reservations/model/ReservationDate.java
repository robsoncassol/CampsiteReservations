package com.upgrade.CampsiteReservations.reservations.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class ReservationDate implements Serializable {

  @EmbeddedId
  private ReservationDateKey key;

  @MapsId("reservationId")
  @ManyToOne(optional = false)
  @JoinColumn(name = "reservation_id",referencedColumnName = "id",insertable = false, updatable = false)
  private Reservation reservation;

  @Column(unique = true,insertable = false, updatable = false)
  private LocalDate day;

  public boolean isBusy() {
    return reservation != null;
  }

  public ReservationDate(LocalDate day, Reservation reservation) {
    this.key = new ReservationDateKey(reservation.getId(),day);
    this.reservation = reservation;
    this.day = day;
  }

  public ReservationDate(LocalDate day) {
    this.key = new ReservationDateKey(null,day);
    this.day = day;
  }

  public LocalDate getDay() {
    return day;
  }

  public void setReservation(Reservation reservation) {
    this.reservation = reservation;
    this.key.setReservationId(reservation.getId());
  }
}
