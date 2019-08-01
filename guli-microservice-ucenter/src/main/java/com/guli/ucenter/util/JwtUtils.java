package com.guli.ucenter.util;


import com.guli.ucenter.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {

    public static final String SUBJECT = "guli-user";

    //秘钥
    public static final String APPSECRET = "guli666";

    //过期时间，毫秒，30分钟
    public static final long EXPIRE = 1000 * 60 * 30;

    public static String generateJWT(Member member){

        String token = Jwts.builder()
                .setSubject(SUBJECT)
                .claim("id", member.getId())
                .claim("nickname", member.getNickname())
                .claim("avatar", member.getAvatar())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256,APPSECRET).compact();

        return token;

    }

    /**
     * 校验jwt
     * @param jwtToken
     * @return
     */
    public static Claims checkJWT(String jwtToken){

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APPSECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();

        return claims;
    }

}
