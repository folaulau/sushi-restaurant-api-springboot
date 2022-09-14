package com.sushi.api.entity.reservation;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private ReservationValidatorService reservationValidatorService;

  @Override
  public ReservationDTO create(ReservationCreateDTO reservationCreateDTO) {

    User user = reservationValidatorService.validateCreate(reservationCreateDTO);

    Reservation reservation =
        entityDTOMapper.mapReservationCreateDTOToReservation(reservationCreateDTO);

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


}
