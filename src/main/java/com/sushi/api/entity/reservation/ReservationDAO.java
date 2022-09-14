package com.sushi.api.entity.reservation;

import java.util.Optional;
import com.sushi.api.dto.ReservationDTO;

public interface ReservationDAO {

  Optional<Reservation> findByUuid(String uuid);

  Reservation save(Reservation reservation);

}
