package com.sushi.api.entity.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.String;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByUuid(String uuid);
	
	Page<Order> findByUserUuid(String userUuid, Pageable pageable);
	
	@Query("select order from Order order where order.paid = false and order.current = true and order.user.id = :userId ")
	Order getCurrentOrderByUserId(@Param("userId") Long userId);
	
	// order placed to order preparing
	Page<Order> findByStatusAndPaidAtBefore(OrderStatus status, LocalDateTime paidAt, Pageable pageable);
	
}
