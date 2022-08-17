package com.sushi.api.entity.account;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface AccountRepository extends JpaRepository<Account, Long> {

  Optional<Account> findById(Long id);

  Optional<Account> findByUuid(String uuid);
}
