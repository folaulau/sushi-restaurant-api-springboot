package com.sushi.api.entity.reservation;

import com.sushi.api.dto.ReservationCreateDTO;
import com.sushi.api.dto.ReservationDTO;
import com.sushi.api.dto.ReservationUpdateDTO;

public interface ReservationService {

  ReservationDTO create(ReservationCreateDTO reservationCreateDTO);

  ReservationDTO update(ReservationUpdateDTO reservationUpdateDTO);

  ReservationDTO getUuid(String uuid);

  Reservation checkIn(String uuid);
  
  Reservation checkIn(Reservation reservation);
  
  Reservation markAsNoShow(String uuid);
  
  Reservation markAsNoShow(Reservation reservation);

}
