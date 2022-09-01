package com.sushi.api.entity.user;

import java.util.Optional;

public interface UserDAO {

  User save(User user);

  Optional<User> findByEmail(String email);

  Optional<User> findById(Long id);

  Optional<User> findByUuid(String uuid);
}
