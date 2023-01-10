package com.jwtApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtApp.model.User;

public interface UserRepo extends JpaRepository<User, Integer>{

	public Optional<User> findByEmail(String email);
	
	public Boolean existsByEmail(String email);
	
	
}
