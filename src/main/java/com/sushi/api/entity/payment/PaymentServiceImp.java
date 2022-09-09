package com.sushi.api.entity.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sushi.api.dto.EntityDTOMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServiceImp implements PaymentService {

	@Autowired
	private EntityDTOMapper entityDTOMapper;
	
	@Autowired
	private PaymentRepository paymentRepository;

}
