package com.sushi.api.entity.reservation;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sushi.api.dto.ReservationCreateDTO;
import com.sushi.api.dto.ReservationUpdateDTO;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserDAO;
import com.sushi.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservationValidatorServiceImp implements ReservationValidatorService {

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private ReservationDAO reservationDAO;

  @Override
  public User validateCreate(ReservationCreateDTO reservationCreateDTO) {

    return userDAO.findByUuid(reservationCreateDTO.getUserUuid()).orElse(null);
  }

  @Override
  public Pair<Reservation, User> validateUpdate(ReservationUpdateDTO reservationUpdateDTO) {

    String uuid = reservationUpdateDTO.getUuid();

    Reservation reservation = reservationDAO.findByUuid(uuid).orElseThrow(
        () -> new ApiException("reservation not found", "reservation not found for uuid=" + uuid));

    User user = userDAO.findByUuid(reservationUpdateDTO.getUserUuid()).orElse(null);

    reservation.setUser(user);

    return Pair.of(reservation, user);
  }


}
