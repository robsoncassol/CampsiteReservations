package com.upgrade.CampsiteReservations.reservations.controller;

import com.upgrade.CampsiteReservations.reservations.dto.ReservationDTO;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("reservation")
public class ReservationController {

  private ReservationService reservationService;
  private ReservationMapper reservationMapper;


  public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
    this.reservationService = reservationService;
    this.reservationMapper = reservationMapper;
  }

  @GetMapping
  public ResponseEntity<List<LocalDate>> listAvailableDates(@RequestParam("from") LocalDate from,
                                                            @RequestParam("until") LocalDate until) {

    List<LocalDate> availableDates = reservationService.getAvailableDates(from, until);
    if (availableDates.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(availableDates);
  }

  @PostMapping
  public ResponseEntity<ReservationDTO> bookCampsite(@RequestBody @Validated ReservationDTO reservationDTO) {
    Reservation reservation = reservationService.bookCampsite(reservationMapper.toEntity(reservationDTO));
    return ResponseEntity.ok(reservationMapper.toDTO(reservation));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ReservationDTO> updateReservation(@PathVariable("id") Long id, @RequestBody @Validated ReservationDTO reservationDTO) {
    return reservationService.getReservationById(id)
        .map(r -> reservationService.updateReservation(r.getId(), reservationMapper.toEntity(reservationDTO)))
        .map(r -> ResponseEntity.ok(reservationMapper.toDTO(r)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("{id}")
  public ResponseEntity cancelReservation(Long id) {
    return reservationService.getReservationById(id)
        .map(r -> reservationService.cancelReservation(r))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }


}
