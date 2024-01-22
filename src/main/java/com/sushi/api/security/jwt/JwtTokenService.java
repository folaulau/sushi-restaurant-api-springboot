package com.sushi.api.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
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
public class JwtTokenService {

    private static final String      secret            = "sushiapi-sushiapi-sushiapi-sushiapi-sushiapi";

    private static final String      audience          = "sushiapi";

    private static final Algorithm   ALGORITHM         = Algorithm.HMAC256(secret);

    private static final String      ISSUER            = "sushiapi";

    /**
     * user can stay logged in for 14 days
     */
    private static final int         LIFE_TIME_IN_DAYS = 200;

    private static final JWTVerifier VERIFIER          = JWT.require(ALGORITHM).withIssuer(ISSUER).build();

    public String generateUserToken(User user) {

        try {

            Map<String, Object> hasura = new HashMap<String, Object>();

            hasura.put("x-hasura-allowed-roles", UserType.getAllAuths());
            hasura.put("x-hasura-default-role", UserType.user.name());
            hasura.put("x-Hasura-user-id", user.getId() + "");
            hasura.put("x-Hasura-user-uuid", user.getUuid());

      // @formatter:off

      String token = JWT.create().withJWTId(RandomGeneratorUtils.getJWTId())
          .withSubject(user.getId() + "")
          .withExpiresAt(DateUtils.addDays(new Date(), LIFE_TIME_IN_DAYS))
          .withIssuedAt(new Date())
          .withAudience(audience).withIssuer(ISSUER)
          .withClaim("uuid", user.getUuid())
          .withClaim("name", user.getFullName())
          .withClaim("role", UserType.user.name())
          .withClaim("hasura", hasura).sign(ALGORITHM);
      
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

    public JwtPayload getPayloadByToken(String token) {
        if (token == null || token.length() == 0) {
            return null;
        }

        try {

            // Reusable verifier instance
            DecodedJWT jwt = VERIFIER.verify(token);
            if (jwt != null) {
                JwtPayload jwtPayload = new JwtPayload();
                jwtPayload.setExp(jwt.getExpiresAt());
                jwtPayload.setIss(jwt.getIssuer());
                jwtPayload.setJti(jwt.getId());
                jwtPayload.setIat(jwt.getIssuedAt());
                jwtPayload.setSub(jwt.getSubject());
                jwtPayload.setAud(jwt.getAudience().get(0));
                jwtPayload.setName(jwt.getClaim("name").asString());
                jwtPayload.setUuid(jwt.getClaim("uuid").asString());
                jwtPayload.setRole(jwt.getClaim("role").asString());

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
