package com.jwtApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jwtApp.jwt.EntryPoint;
import com.jwtApp.jwt.JwtAuthTokenFilter;
import com.jwtApp.service.UserServiceImpl;

@Configuration
public class SecurityConfig {

	@Autowired
	private JwtAuthTokenFilter authenticationJwtTokenFilter;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private EntryPoint entryPoint;
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.cors().and().csrf().disable()
        .exceptionHandling().authenticationEntryPoint(entryPoint).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeHttpRequests().requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/test/**").permitAll()
        .anyRequest().authenticated();
    
	    http.authenticationProvider(daoAuthenticationProvider());
	    
	    http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	    
	    return http.build();

	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider () {
		DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
		dao.setUserDetailsService(userServiceImpl);
		dao.setPasswordEncoder(passwordEncoder());
		return dao;
	}
	
	@Bean
	public JwtAuthTokenFilter authenticationJwtTokenFilter() {
	   return new JwtAuthTokenFilter();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder () {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
		return auth.getAuthenticationManager();
	}
	
}
