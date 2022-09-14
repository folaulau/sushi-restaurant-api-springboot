package com.sushi.api.entity.reservation;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Optional<Reservation> findByUuid(String uuid);
}
