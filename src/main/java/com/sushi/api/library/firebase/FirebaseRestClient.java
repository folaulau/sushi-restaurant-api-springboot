package com.sushi.api.library.firebase;

import org.springframework.scheduling.annotation.Async;

public interface FirebaseRestClient {

  public FirebaseAuthResponse signUp(String email, String password, boolean returnSecureToken);

  public FirebaseAuthResponse signUp(String email, String password);

  @Async
  public void signUpAsync(String email, String password);

  public FirebaseAuthResponse signIn(String email, String password, boolean returnSecureToken);

  public FirebaseAuthResponse signIn(String email, String password);
}
