package com.upgrade.CampsiteReservations.reservations.controller;

import com.upgrade.CampsiteReservations.reservations.dto.ReservationDTO;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Service
@Mapper
public interface ReservationMapper {


  ReservationDTO toDTO(Reservation reservation);

  Reservation toEntity(ReservationDTO reservationDTO);

}
