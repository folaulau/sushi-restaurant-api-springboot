package com.sushi.api.entity.order.lineitem;

import java.util.Optional;

public interface LineItemDAO {

  LineItem save(LineItem lineItem);
  
  Optional<LineItem> getByUuid(String uuid);
  
  LineItem getLineItemByUidAndOrderUid(String uid, String orderUid);
  
  LineItem getLineItemByUidAndOrderId(String uid, Long orderId);
  
  LineItem getLineItemByOrderUidAndProductUid(String orderUid, String productUid);

}
