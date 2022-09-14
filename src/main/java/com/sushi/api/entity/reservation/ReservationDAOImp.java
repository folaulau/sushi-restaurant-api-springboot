package com.sushi.api.entity.reservation;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.sushi.api.dto.ReservationDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ReservationDAOImp implements ReservationDAO {

  @Autowired
  private ReservationRepository reservationRepository;

  @Override
  public Reservation save(Reservation reservation) {
    return reservationRepository.saveAndFlush(reservation);
  }
  
  @Override
  public Optional<Reservation> findByUuid(String uuid) {
    return reservationRepository.findByUuid(uuid);
  }


}
