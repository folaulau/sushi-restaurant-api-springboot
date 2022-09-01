package com.sushi.api.library.firebase;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.sushi.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirebaseAuthServiceImp implements FirebaseAuthService {

  @Autowired
  private FirebaseAuth firebaseAuth;

  @Override
  public FirebaseToken verifyToken(String firebaseToken) {
    log.info("verifyToken({})", firebaseToken);
    try {
      return firebaseAuth.verifyIdToken(firebaseToken, true);
    } catch (FirebaseAuthException e) {
      log.warn("Firebase verify token error={}", e.getMessage());

      throw new ApiException("Unable to sign in", "Firebase verify token error=" + e.getMessage());
    }
  }

  @Override
  public Optional<UserRecord> getFirebaseUser(String uuid) {
    log.info("getFirebaseUser({})", uuid);
    UserRecord userRecord = null;
    try {
      userRecord = firebaseAuth.getUser(uuid);
    } catch (FirebaseAuthException e) {
      log.warn("Firebase getUser error={}", e.getMessage());
    }
    return Optional.ofNullable(userRecord);
  }

  @Override
  public UserRecord verifyAndGetUser(String firebaseToken) {
    FirebaseToken fbToken = verifyToken(firebaseToken);
    return getFirebaseUser(fbToken.getUid()).get();
  }

  @Async
  @Override
  public void createUser(String email, String password, String displayName) {
    CreateRequest createRequest = new CreateRequest();
    createRequest.setEmail(email);
    createRequest.setPassword(password);
    createRequest.setDisplayName(displayName);
    try {
      firebaseAuth.createUser(createRequest);
    } catch (FirebaseAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void updateEmailAndPassword(String uuid, String password, String email) {
    UpdateRequest updateRequest = new UpdateRequest(uuid);
    updateRequest.setPassword(password);
    updateRequest.setEmail(email);

    try {
      firebaseAuth.updateUser(updateRequest);
    } catch (FirebaseAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
