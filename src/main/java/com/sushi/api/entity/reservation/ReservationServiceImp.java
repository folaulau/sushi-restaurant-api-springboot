package com.sushi.api.entity.reservation;

import java.time.LocalDateTime;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sushi.api.dto.EntityDTOMapper;
import com.sushi.api.dto.ReservationCreateDTO;
import com.sushi.api.dto.ReservationDTO;
import com.sushi.api.dto.ReservationUpdateDTO;
import com.sushi.api.entity.user.User;
import com.sushi.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservationServiceImp implements ReservationService {

  @Autowired
  private ReservationDAO reservationDAO;

  @Autowired
  private EntityDTOMapper entityDTOMapper;
  
  /**
   * 2 hours
   */
  @Value("${reservation.estimate.duration:2}")
  private int reservationEstimateDuration;

  @Autowired
  private ReservationValidatorService reservationValidatorService;

  @Override
  public ReservationDTO create(ReservationCreateDTO reservationCreateDTO) {

    User user = reservationValidatorService.validateCreate(reservationCreateDTO);

    Reservation reservation =
        entityDTOMapper.mapReservationCreateDTOToReservation(reservationCreateDTO);

    reservation.setUser(user);
    reservation.setStatus(ReservationStatus.RESERVED);
    reservation.setReservedAt(LocalDateTime.now());
    
    LocalDateTime dateTime = reservation.getDateTime();
    
    reservation.setEstimatedFinishedTime(dateTime.plusHours(reservationEstimateDuration));

    reservation = reservationDAO.save(reservation);

    return entityDTOMapper.mapReservationToReservationDTO(reservation);
  }

  @Override
  public ReservationDTO update(ReservationUpdateDTO reservationUpdateDTO) {

    Pair<Reservation, User> pair = reservationValidatorService.validateUpdate(reservationUpdateDTO);

    Reservation reservation = pair.getLeft();

    User user = pair.getRight();

    reservation.setUser(user);

    reservation =
        entityDTOMapper.patchReservationWithReservationUpdateDTO(reservationUpdateDTO, reservation);

    reservation = reservationDAO.save(reservation);

    return entityDTOMapper.mapReservationToReservationDTO(reservation);
  }

  @Override
  public ReservationDTO getUuid(String uuid) {
    Reservation reservation = reservationDAO.findByUuid(uuid).orElseThrow(
        () -> new ApiException("Reservation not found", "Reservation not found for uuid=" + uuid));
    return entityDTOMapper.mapReservationToReservationDTO(reservation);
  }

  @Override
  public Reservation checkIn(String uuid) {
    Reservation reservation = reservationDAO.findByUuid(uuid).orElseThrow(
        () -> new ApiException("Reservation not found", "Reservation not found for uuid=" + uuid));
    return checkIn(reservation);
  }
  

  @Override
  public Reservation checkIn(Reservation reservation) {
    reservation.setStatus(ReservationStatus.CHECKED_IN);
    reservation.setCheckedInTime(LocalDateTime.now());
    return reservationDAO.save(reservation);
  }

  @Override
  public Reservation markAsNoShow(String uuid) {
    Reservation reservation = reservationDAO.findByUuid(uuid).orElseThrow(
        () -> new ApiException("Reservation not found", "Reservation not found for uuid=" + uuid));
    return markAsNoShow(reservation);
  }

  @Override
  public Reservation markAsNoShow(Reservation reservation) {
    reservation.setStatus(ReservationStatus.NO_SHOW);
    return reservationDAO.save(reservation);
  }


}
