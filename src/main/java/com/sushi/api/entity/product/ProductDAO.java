package com.sushi.api.entity.product;

import java.util.Optional;

public interface ProductDAO {

  Product save(Product product);

  Optional<Product> getByUuid(ProductName uuid);
}
