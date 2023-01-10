package com.jwtApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtApp.model.MyRole;
import com.jwtApp.model.Role;

public interface RoleRepo extends JpaRepository<Role, Integer>{

	public Optional<Role> findByRole(MyRole role);
}
