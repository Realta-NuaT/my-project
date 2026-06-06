package org.example.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    private String jwtKey;

    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate template;

    public boolean invalidateJwt(String headerToken) {
        if (headerToken == null) return false;
        String token = this.convertToken(headerToken);
        if(token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(jwtKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());
        }catch (JWTVerificationException exception){
            return false;
        }
    }
    private boolean deleteToken(String uuid, Date time) {
        if(this.isInvalidToken(uuid)) return false;
        Date date = new Date();
        long expire = Math.max(time.getTime() - date.getTime(), 0);
        template.opsForValue().set(Const.JWT_BACK_LIST + uuid, "",expire, TimeUnit.MILLISECONDS);
        return true;
    }
    private boolean isInvalidToken(String uuid) {
        return template.hasKey(Const.JWT_BACK_LIST + uuid);
    }

    public DecodedJWT resolveJwt(String headerToken){
        if (headerToken == null) return null;
        String token = this.convertToken(headerToken);
        if(token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(jwtKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
            DecodedJWT decodedJWT = verifier.verify(token);
            if(this.isInvalidToken(decodedJWT.getId()))
                return null;
            Date expiresAt = decodedJWT.getExpiresAt();
            return new Date().after(expiresAt) ? null : decodedJWT;
        }catch(JWTVerificationException e){
            return null;
        }
    }

    public String createJwt(UserDetails userDetails ,int id ,String username) {
        Algorithm algorithm = Algorithm.HMAC256(jwtKey);
        Date date = this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("authorities",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }
    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24);
        return calendar.getTime();
    }

    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("**********")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }
    public int toId(DecodedJWT jwt){
        return jwt.getClaim("id").asInt();
    }
    private String convertToken(String headerToken){
        if(!headerToken.startsWith("Bearer ") || headerToken == null){
            return null;
        }
        return headerToken.substring(7);
    }
}
