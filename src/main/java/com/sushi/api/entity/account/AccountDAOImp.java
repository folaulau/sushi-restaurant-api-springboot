package com.sushi.api.entity.account;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AccountDAOImp implements AccountDAO {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public Account save(Account account) {
    return accountRepository.saveAndFlush(account);
  }

  @Override
  public Optional<Account> getById(Long id) {
    return accountRepository.findById(id);
  }

  @Override
  public Optional<Account> getByUuid(String uuid) {
    return accountRepository.findByUuid(uuid);
  }
}
