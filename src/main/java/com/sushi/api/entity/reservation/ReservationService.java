package com.sushi.api.entity.reservation;

import javax.validation.Valid;
import com.sushi.api.dto.ReservationCreateDTO;
import com.sushi.api.dto.ReservationDTO;
import com.sushi.api.dto.ReservationUpdateDTO;

public interface ReservationService {

  ReservationDTO create(ReservationCreateDTO reservationCreateDTO);

  ReservationDTO update(ReservationUpdateDTO reservationUpdateDTO);

  ReservationDTO getUuid(String uuid);

}
