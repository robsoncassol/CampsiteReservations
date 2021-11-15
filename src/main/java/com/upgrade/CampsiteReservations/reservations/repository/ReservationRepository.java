package com.upgrade.CampsiteReservations.reservations.repository;

import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {


}
