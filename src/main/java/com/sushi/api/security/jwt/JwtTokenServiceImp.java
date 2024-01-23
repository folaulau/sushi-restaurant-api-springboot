package com.sushi.api.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sushi.api.entity.role.UserType;
import com.sushi.api.entity.user.User;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenServiceImp implements JwtTokenService {

    @Value("${jwt.secret}")
    private String      secret;

    @Value("${jwt.issuer}")
    private String      issuer;
    
    @Value("${jwt.audience}")
    private String      audience;

    /**
     * user can stay logged in for 2 days
     */
    private final int   LIFE_TIME_IN_DAYS = 14;

    private Algorithm   algorithm;

    private JWTVerifier jwtVerifier;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secret);
        jwtVerifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    @Override
    public String generateUserToken(User user) {

        try {

            Map<String, Object> hasura = new HashMap<String, Object>();

            hasura.put("x-hasura-allowed-roles", UserType.getAllAuths());
            hasura.put("x-hasura-default-role", UserType.user.name());
            hasura.put("x-Hasura-user-id", user.getId() + "");
            hasura.put("x-Hasura-user-uuid", user.getUuid());

      // @formatter:off

      String token = JWT.create()
          .withJWTId(RandomGeneratorUtils.getJWTId())
          .withSubject(user.getId() + "")
          .withIssuer(issuer)
          .withExpiresAt(DateUtils.addDays(new Date(), LIFE_TIME_IN_DAYS))
          .withIssuedAt(new Date())
          .withAudience(audience)
          .withClaim("uuid", user.getUuid())
          .withClaim("name", user.getFullName())
          .withClaim("role", UserType.user.name())
          .withClaim("hasura", hasura)
          
          .sign(algorithm);
      
      // @formatter:on

            return token;
        } catch (JWTCreationException e) {
            log.warn("JWTCreationException, msg: {}", e.getLocalizedMessage());
            return null;
        } catch (Exception e) {
            log.warn("generateToken exception, msg: {}", e.getLocalizedMessage());
            return null;
        }

    }

    @Override
    public JwtPayload getPayloadByToken(String token) {
        if (token == null || token.length() == 0) {
            return null;
        }

        try {

            // Reusable verifier instance
            DecodedJWT jwt = jwtVerifier.verify(token);
            if (jwt != null) {
                JwtPayload jwtPayload = new JwtPayload();
                jwtPayload.setExp(jwt.getExpiresAt());
                jwtPayload.setIss(jwt.getIssuer());
                jwtPayload.setJti(jwt.getId());
                jwtPayload.setIat(jwt.getIssuedAt());
                jwtPayload.setSub(jwt.getSubject());
                jwtPayload.setAud(jwt.getAudience().get(0));
                jwtPayload.setName(jwt.getClaim("name").asString());
                jwtPayload.setRole(jwt.getClaim("role").asString());
                jwtPayload.setUuid(jwt.getClaim("uuid").asString());

                setHasura(jwtPayload, jwt);

                return jwtPayload;
            }
        } catch (Exception e) {
            log.warn("getPayloadByToken exception, msg: {}", e.getLocalizedMessage());
        }
        return null;
    }

    private void setHasura(JwtPayload jwtPayload, DecodedJWT jwt) {
        Map<String, Object> hasura = null;
        try {
            hasura = jwt.getClaim("hasura").asMap();
            jwtPayload.setHasura(hasura);
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
        }

        if (hasura == null) {
            return;
        }

    }
}
