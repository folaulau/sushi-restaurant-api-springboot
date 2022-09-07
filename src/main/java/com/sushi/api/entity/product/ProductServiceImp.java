package com.sushi.api.entity.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductServiceImp implements ProductService {

  @Autowired
  private ProductDAO productDAO;


}
