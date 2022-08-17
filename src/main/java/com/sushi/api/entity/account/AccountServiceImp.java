package com.sushi.api.entity.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountServiceImp implements AccountService {

  @Autowired
  private AccountDAO accountDAO;


}
