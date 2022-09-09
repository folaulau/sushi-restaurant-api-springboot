package com.sushi.api.entity.order.lineitem;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class LineItemDAOImp implements LineItemDAO {

  @Autowired
  private LineItemRepository lineItemRepository;

  @Override
  public LineItem save(LineItem lineItem) {
    return lineItemRepository.saveAndFlush(lineItem);
  }

  @Override
  public Optional<LineItem> getByUuid(String uid) {
    return lineItemRepository.findByUuid(uid);
  }

  @Override
  public LineItem getLineItemByUidAndOrderUid(String uid, String orderUid) {
    return lineItemRepository.findByUuidAndOrderUuid(uid, orderUid);
  }

  @Override
  public LineItem getLineItemByUidAndOrderId(String uid, Long orderId) {
    return this.lineItemRepository.findByUuidAndOrderId(uid, orderId);
  }

  @Override
  public LineItem getLineItemByOrderUidAndProductUid(String orderUid, String productUid) {
    return lineItemRepository.findByProductUuidAndOrderUuid(productUid, orderUid);
  }
}
