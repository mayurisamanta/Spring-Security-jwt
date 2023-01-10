package com.jwtApp.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwtApp.dto.JwtResponse;
import com.jwtApp.dto.LoginRequest;
import com.jwtApp.dto.MessageResponse;
import com.jwtApp.dto.ResponseMessage;
import com.jwtApp.dto.SignUpRequest;
import com.jwtApp.jwt.JwtUtil;
import com.jwtApp.model.MyRole;
import com.jwtApp.model.Role;
import com.jwtApp.model.User;
import com.jwtApp.repository.RoleRepo;
import com.jwtApp.repository.UserRepo;
import com.jwtApp.service.UserDetailsImpl;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class MyController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private RoleRepo rRepo;
	
	@Autowired
	private UserRepo uRepo;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody SignUpRequest signUpRequest){
		
		if (uRepo.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new ResponseMessage("User already exists"));
		}
		else {
			User user = new User();
			user.setEmail(signUpRequest.getEmail());
			user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
			user.setName(signUpRequest.getName());
			
			Set<String> strRoles = signUpRequest.getRole();
			Set<Role> roles = new HashSet<>();

			if (strRoles == null) {
				Role userRole = rRepo.findByRole(MyRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Role not found"));
				roles.add(userRole);
			} else {
				strRoles.forEach(role -> {
					switch (role) {
					case "admin":
						Role adminRole = rRepo.findByRole(MyRole.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);

						break;
					case "mod":
						Role modRole = rRepo.findByRole(MyRole.ROLE_MODERATOR)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(modRole);

						break;
					default:
						Role userRole = rRepo.findByRole(MyRole.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
					}
				});
			}

			user.setRoles(roles);
			uRepo.save(user);

			return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

		
		
		}
	}	
	
	@PostMapping("/login")
	public ResponseEntity<?> login (LoginRequest loginRequest){
		Authentication auth =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), 
				loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtUtil.generateJwtToken(auth);
		
		UserDetailsImpl detailsImpl =  (UserDetailsImpl) auth.getPrincipal();
		
		List<String> roles = detailsImpl.getAuthorities().stream().map(i -> i.getAuthority()).collect(Collectors.toList());
		
		return ResponseEntity.ok(new JwtResponse(jwtToken, detailsImpl.getId(), detailsImpl.getEmail(), roles));
		
		
		
		
		
	}
}
