package com.jwtApp.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jwtApp.service.UserServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter{

	@Autowired
	private UserServiceImpl uSer;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			String jwtToken = parseJwt(request);
			
			if (jwtToken != null && jwtUtil.validateJwtToken(jwtToken)) {
				String username = jwtUtil.getUsernameFromJwtToken(jwtToken);
				UserDetails userDetail = uSer.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetail, null, 
						userDetail.getAuthorities());
				
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(auth);
				
			}
			
		}
		catch (Exception ex) {
			logger.error("Cannot set user authentication: {}", ex);
		}
		
		filterChain.doFilter(request, response);
	}
	
	
	public String parseJwt(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		
		if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			return header.substring(7, header.length());
		}
		return null;
	}
}
