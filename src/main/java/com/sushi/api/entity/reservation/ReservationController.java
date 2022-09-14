package com.sushi.api.entity.reservation;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sushi.api.dto.ReservationCreateDTO;
import com.sushi.api.dto.ReservationDTO;
import com.sushi.api.dto.ReservationUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Reservations", description = "Reservation Operations")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

  @Autowired
  private ReservationService reservationService;


  @Operation(summary = "Create Reservation", description = "create reservation")
  @PostMapping("/reservation")
  public ResponseEntity<ReservationDTO> create(@Parameter(name = "Reservation", required = true,
      example = "reservation") @Valid @RequestBody ReservationCreateDTO reservationCreateDTO) {
    log.debug("create({})", reservationCreateDTO.toJson());

    ReservationDTO reservationDTO = reservationService.create(reservationCreateDTO);

    return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
  }

  @Operation(summary = "Update Reservation", description = "update reservation")
  @PutMapping("/reservation")
  public ResponseEntity<ReservationDTO> update(@Parameter(name = "Reservation", required = true,
      example = "reservation") @Valid @RequestBody ReservationUpdateDTO reservationUpdateDTO) {
    log.debug("update({})", reservationUpdateDTO.toJson());

    ReservationDTO reservationDTO = reservationService.update(reservationUpdateDTO);

    return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
  }

  @Operation(summary = "Create Guest Reservation", description = "create guest reservation")
  @PostMapping("/guest/reservation")
  public ResponseEntity<ReservationDTO> createGuest(
      @Parameter(name = "Reservation", required = true,
          example = "reservation") @Valid @RequestBody ReservationCreateDTO reservationCreateDTO) {
    log.debug("createGuest({})", reservationCreateDTO.toJson());

    ReservationDTO reservationDTO = reservationService.create(reservationCreateDTO);

    return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
  }

  @Operation(summary = "Update Guest Reservation", description = "update guest reservation")
  @PutMapping("/guest/reservation")
  public ResponseEntity<ReservationDTO> updateGuest(
      @Parameter(name = "Reservation", required = true,
          example = "reservation") @Valid @RequestBody ReservationUpdateDTO reservationUpdateDTO) {
    log.debug("updateGuest({})", reservationUpdateDTO.toJson());

    ReservationDTO reservationDTO = reservationService.update(reservationUpdateDTO);

    return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
  }

  @Operation(summary = "Get Reservation", description = "get reservation")
  @GetMapping("/guest/reservation")
  public ResponseEntity<ReservationDTO> getByUuid(
      @Parameter(name = "uuid", required = true, example = "uuid") @RequestParam String uuid) {
    log.debug("getByUuid({})", uuid);

    ReservationDTO reservationDTO = reservationService.getUuid(uuid);

    return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
  }
}
