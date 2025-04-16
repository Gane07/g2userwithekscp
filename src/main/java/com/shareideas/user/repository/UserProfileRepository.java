package com.shareideas.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shareideas.user.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile,String>{
	
	public Optional<UserProfile> getByUsername(String username);

}
