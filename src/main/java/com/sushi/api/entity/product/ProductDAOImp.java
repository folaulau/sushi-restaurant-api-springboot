package com.sushi.api.entity.product;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ProductDAOImp implements ProductDAO {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public Product save(Product product) {
    return productRepository.saveAndFlush(product);
  }

  @Override
  public Optional<Product> getByUuid(ProductName uuid) {
    return productRepository.findByUuid(uuid);
  }
}
