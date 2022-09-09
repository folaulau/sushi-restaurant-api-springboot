package com.sushi.api.entity.order.lineitem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LineItemServiceImp implements LineItemService {

  @Autowired
  private LineItemRepository lineItemRepository;



}
