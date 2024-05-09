package com.oneinstep.starter.security.jwt;


import com.oneinstep.starter.security.bean.bo.TokenUserInfoBO;
import com.oneinstep.starter.security.properties.AuthProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.oneinstep.starter.security.constant.SecurityConstants.STR_SYSTEM_TYPE;

/**
 * Jwt token provider
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Resource
    private AuthProperties authProperties;

    /**
     * 生成 Jwt token
     *
     * @param tokenUserInfoBO 账户信息
     * @return token
     */
    public String generateToken(TokenUserInfoBO tokenUserInfoBO) {

        Long userId = tokenUserInfoBO.getUserId();

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + authProperties.getJwtExpiration());


        Map<String, Object> claims = new HashMap<>();
        claims.put(STR_SYSTEM_TYPE, tokenUserInfoBO.getSystemType().getCode());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(authProperties.getJwtSecret())
        );
    }

    // get username from Jwt token
    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public Integer getAccountType(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get(STR_SYSTEM_TYPE, Integer.class);
    }

    // validate Jwt token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
