package com.sushi.api.entity.order.lineitem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.lang.String;
import java.util.Optional;

public interface LineItemRepository extends JpaRepository<LineItem, Long> {

	Optional<LineItem> findByUuid(String uuid);
	
	LineItem findByUuidAndOrderUuid(String uuid, String orderUuid);
	
	LineItem findByUuidAndOrderId(String uuid, Long orderId);
	
	LineItem findByProductUuidAndOrderUuid(String productUuid, String orderUuid);
}
