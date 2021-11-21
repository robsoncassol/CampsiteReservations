package com.upgrade.CampsiteReservations.reservations.controller;

import com.upgrade.CampsiteReservations.exceptions.ExceptionDTO;
import com.upgrade.CampsiteReservations.reservations.dto.AvailableDateDTO;
import com.upgrade.CampsiteReservations.reservations.dto.ReservationDTO;
import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.service.ReservationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.Optional;

@RestController
@RequestMapping("reservations")
public class ReservationController {

  private ReservationService reservationService;
  private ReservationMapper reservationMapper;

  public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
    this.reservationService = reservationService;
    this.reservationMapper = reservationMapper;
  }

  @ApiOperation(
      value = "Get all available dates in the selected period",
      nickname = "getAvailableDates",
      response = AvailableDateDTO.class
  )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Available dates retrieved successfully", response = LocalDate.class),
      @ApiResponse(code = 400, message = "selected period is invalid", response = AvailableDateDTO.class)
  })
  @GetMapping
  public ResponseEntity<List<AvailableDateDTO>> getAvailableDates(
      @ApiParam(value = "Start date of desired period (expected format ISO-8601)")
      @RequestParam("from") LocalDate from,
      @ApiParam(value = "End date of desired period (expected format ISO-8601)")
      @RequestParam("until") LocalDate until) {

    return ResponseEntity.ok(reservationService.getAvailableDates(from, until));
  }

  @ApiOperation(
      value = "Book the campsite for the selected dates",
      nickname = "bookCampsite",
      response = ReservationDTO.class
  )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Return a ReservationDTO with an unique id", response = ReservationDTO.class),
      @ApiResponse(code = 400, message = "Invalid parameters", response = ExceptionDTO.class)
  })
  @PostMapping
  public ResponseEntity<ReservationDTO> bookCampsite(
          @ApiParam(value = "Reservation data", example = "{\n" +
              "  \"arrivalDate\": \"2021-11-20\",\n" +
              "  \"departureDate\": \"2021-11-23\",\n" +
              "  \"email\": \"john@mail.com\",\n" +
              "  \"name\": \"John Smith\"\n" +
              "}")
          @RequestBody
          @Validated
          ReservationDTO reservationDTO) {
    Reservation reservation = reservationService.bookCampsite(reservationMapper.toEntity(reservationDTO));
    return ResponseEntity.ok(reservationMapper.toDTO(reservation));
  }

  @ApiOperation(
      value = "Get the reservation by id",
      nickname = "getReservationById",
      response = ReservationDTO.class
  )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success return a ReservationDTO", response = ReservationDTO.class),
      @ApiResponse(code = 404, message = "Resource not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ReservationDTO> getReservationById(@PathVariable("id") Long id) {
    return reservationService.getReservationById(id)
        .map(r -> ResponseEntity.ok(reservationMapper.toDTO(r)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @ApiOperation(
      value = "Update the reservation",
      nickname = "updateReservation",
      response = ReservationDTO.class
  )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success return a ReservationDTO", response = ReservationDTO.class),
      @ApiResponse(code = 404, message = "Resource not found"),
      @ApiResponse(code = 400, message = "Invalid parameters", response = ExceptionDTO.class)
  })
  @PutMapping("/{id}")
  public ResponseEntity<ReservationDTO> updateReservation(@PathVariable("id") Long id, @RequestBody @Validated ReservationDTO reservationDTO) {
    return reservationService.getReservationById(id)
        .map(r -> reservationService.updateReservation(r.getId(), reservationMapper.toEntity(reservationDTO)))
        .map(r -> ResponseEntity.ok(reservationMapper.toDTO(r)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @ApiOperation(
      value = "Cancel the reservation",
      nickname = "cancelReservation"
  )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Reservation was successfully cancelled", response = ReservationDTO.class),
      @ApiResponse(code = 404, message = "Resource not found")
  })

  @DeleteMapping("/{id}")
  public ResponseEntity cancelReservation(@PathVariable("id") Long id) {
    Optional<Reservation> optionalReservation = reservationService.getReservationById(id);
    if(optionalReservation.isEmpty()){
      return ResponseEntity.notFound().build();
    }
    reservationService.cancelReservation(optionalReservation.get());
    return ResponseEntity.ok().build();
  }


}
