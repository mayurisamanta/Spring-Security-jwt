package com.jwtApp.dto;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

	private String name;
	
	private String password;
	
	private Set<String> role;
	
	@Email
	@Column(unique = true)
	private String email;
}
