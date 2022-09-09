package com.sushi.api.entity.paymentmethod;

import org.springframework.data.jpa.repository.JpaRepository;
import java.lang.String;
import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

	PaymentMethod findByUuid(String uuid);
	
	List<PaymentMethod> findByUserId(Long id);
	
	List<PaymentMethod> findByUserUuid(String uuid);
}
