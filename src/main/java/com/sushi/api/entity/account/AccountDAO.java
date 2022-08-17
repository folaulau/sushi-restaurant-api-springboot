package com.sushi.api.entity.account;

import java.util.Optional;

public interface AccountDAO {

  Account save(Account account);

  Optional<Account> getById(Long id);

  Optional<Account> getByUuid(String uuid);

}
