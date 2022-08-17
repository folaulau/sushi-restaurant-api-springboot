package com.sushi.api.entity.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, Long> {


  Optional<User> findById(Long id);

  Optional<User> findByUuid(String uuid);

}
