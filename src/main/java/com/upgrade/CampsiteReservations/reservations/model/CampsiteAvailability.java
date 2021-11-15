package com.upgrade.CampsiteReservations.reservations.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteAvailability implements Serializable {

  @Id
  private LocalDate day;

  @ManyToOne
  @JoinColumn(name = "reservation_id")
  private Reservation reservation;

  public boolean isBusy() {
    return reservation != null;
  }
}
