package com.sushi.api.entity.reservation;

import org.apache.commons.lang3.tuple.Pair;
import com.sushi.api.dto.ReservationCreateDTO;
import com.sushi.api.dto.ReservationUpdateDTO;
import com.sushi.api.entity.user.User;

public interface ReservationValidatorService {

  User validateCreate(ReservationCreateDTO reservationCreateDTO);

  Pair<Reservation, User> validateUpdate(ReservationUpdateDTO reservationUpdateDTO);
}
