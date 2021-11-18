package com.upgrade.CampsiteReservations;

import com.upgrade.CampsiteReservations.reservations.model.Reservation;
import com.upgrade.CampsiteReservations.reservations.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
@EnableCaching
public class CampsiteReservationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampsiteReservationsApplication.class, args);
	}

	@Autowired
	private ReservationService service;


}
