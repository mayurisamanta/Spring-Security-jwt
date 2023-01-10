package com.jwtApp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

	private String token;
	
	private String type = "Bearer";
	
	private Integer id;
		
	private String email;
	
	private List<String> roles;

	public JwtResponse(String token, Integer id, String email, List<String> roles) {
		super();
		this.token = token;
		this.id = id;
		this.email = email;
		this.roles = roles;
	}
	
	

	
	
}
