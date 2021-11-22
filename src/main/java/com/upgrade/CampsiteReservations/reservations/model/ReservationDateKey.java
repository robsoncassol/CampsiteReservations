package com.upgrade.CampsiteReservations.reservations.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ReservationDateKey implements Serializable {

  @Column(name = "reservation_id")
  private Long reservationId;

  @Column(name = "day")
  private LocalDate day;


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReservationDateKey that = (ReservationDateKey) o;
    return Objects.equals(reservationId, that.reservationId) && Objects.equals(day, that.day);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reservationId, day);
  }


}
