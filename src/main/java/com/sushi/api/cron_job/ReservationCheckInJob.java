package com.sushi.api.cron_job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.sushi.api.entity.order.DeliveryMethod;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderRepository;
import com.sushi.api.entity.order.OrderService;
import com.sushi.api.entity.order.OrderStatus;
import com.sushi.api.entity.reservation.Reservation;
import com.sushi.api.entity.reservation.ReservationDAO;
import com.sushi.api.entity.reservation.ReservationRepository;
import com.sushi.api.entity.reservation.ReservationService;
import com.sushi.api.entity.reservation.ReservationStatus;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReservationCheckInJob {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private ReservationService reservationService;

  /*
   * every minute check for Orders that have been placed to start preping
   */
  @Scheduled(fixedRate = 1000 * 60)
  public void checkInReservation() {
    log.info("checkInReservation {}", LocalDateTime.now());

    int pageNumber = 0;
    int pageSize = 20;
    Pageable page = PageRequest.of(pageNumber, pageSize);
    Page<Reservation> result = null;

    /**
     * 
     */
    LocalDateTime deliveryTime = LocalDateTime.now();

    while (true) {

      result = reservationRepository.findByStatusAndDateTimeBefore(ReservationStatus.RESERVED, deliveryTime, page);

      List<Reservation> reservations = result.getContent();

      reservations.stream().forEach(reservation -> {

        reservation = reservationService.checkIn(reservation);

        log.info("reservation={}", reservation.toJson());
      });

      if (result.hasNext()) {
        ++pageNumber;
        page = PageRequest.of(pageNumber, pageSize);
      } else {
        break;
      }

    }
  }
}
