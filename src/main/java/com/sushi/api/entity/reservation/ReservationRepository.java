package com.sushi.api.entity.reservation;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Optional<Reservation> findByUuid(String uuid);

  Page<Reservation> findByStatusAndDateTimeBefore(ReservationStatus status, LocalDateTime dateTime,
      Pageable page);
}
