package com.inetum.realdolmen.hubkitbackend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY= "Qc10J0K50p4bX6uY9x/ubf4jTZtc3n6uVNZOnKSrP/yndOFJamS3UG0SXAYtG7ErNtFVQ7+5n8s89F6662w52CUx9eK9iFph/u182kZBVTKH5/r3Zfa+sZZP6CEfJ8Ud5kTjr9yAcdxyfussiB7qvuPdCefyDXPOZsUK8GzEtH43FPGVDwFqj6GTF6VeaMpOsbRYgr7JSkPmDTHn91mQ1JYAvw1Yupd+O2/WCbgahlXqzHz8DA2g30VVoc5l72rc9M3jtK/JtBfyMbBhAGUpMB77a4BU0QTFZe7/dnLbd67QLpfmCvOZEiTXvugA5SEjOJnHabESvV4fFB3l2/46nLrQu8neUJfBLCCAPet5guM=\n";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //used to extract one claim from the claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims= extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //If no extra claims are provided
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) //set how long the token is available
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username= extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //Uses the signing key defined to parse the JWT token and extract the claims inside it
    //The key has to be 256 characters long
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(){
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
