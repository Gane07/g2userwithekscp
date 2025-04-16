package com.shareideas.user.service;

import java.util.List;

import com.shareideas.user.model.ChangePasswordDTO;
import com.shareideas.user.model.Credentials;
import com.shareideas.user.model.UserProfile;

public interface UserProfileService {
	
	public UserProfile saveUser(UserProfile userProfile);
	public UserProfile getUser(String username);
	public List<UserProfile> getAll();
	public UserProfile updateProfile(UserProfile userProfile);
	public boolean deleteProfile(String username);
	public boolean authenticateUser(Credentials credentials);
	public String forgotPassword(String username);
	public boolean changePassword(ChangePasswordDTO changePassword);

}
