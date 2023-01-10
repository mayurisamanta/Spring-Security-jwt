package com.jwtApp.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.jwtApp.service.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {
	
	@Value("${jwtApp.app.jwtSecret}")
	private String secretKey;
	
	@Value("${jwtApp.app.jwtExpirationMs}")
	private Integer expirationInMs;

	public String generateJwtToken(Authentication auth) {
		UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
		
		return Jwts.builder()
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + expirationInMs))
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
				
	}
	
	public String getUsernameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}
	
	public Boolean validateJwtToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		}
		catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} 
		catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} 
		catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} 
		catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} 
		catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		
		return false;

	}
}
