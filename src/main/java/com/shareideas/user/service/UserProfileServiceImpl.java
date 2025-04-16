package com.shareideas.user.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.shareideas.user.model.ChangePasswordDTO;
import com.shareideas.user.model.Credentials;
import com.shareideas.user.model.UserProfile;
import com.shareideas.user.repository.UserProfileRepository;

@Service
public class UserProfileServiceImpl implements UserProfileService{
	
	@Autowired
	UserProfileRepository repo;
	
	@Autowired
    private RestTemplate restTemplate;

	@Override
	public UserProfile saveUser(UserProfile userProfile) {
		
		UserProfile user = (UserProfile) repo.save(userProfile);
		
		return user;
	}

	@Override
	public UserProfile getUser(String username) {
		
		Optional<UserProfile> obj = repo.getByUsername(username);
		
		if(obj.isPresent()) {
			UserProfile user = obj.get();
			return user;
		}
		else {
			return null;
		}
		
	}

	@Override
	public List<UserProfile> getAll() {

		List<UserProfile> list = repo.findAll();
		
		return list;
	}

	@Override
	public UserProfile updateProfile(UserProfile userProfile) {
		
		Optional<UserProfile> optional = repo.getByUsername(userProfile.getUsername());
		if(optional.isPresent()) {
			UserProfile profile = optional.get();
			profile.setEmail(userProfile.getEmail());
			profile.setFirstName(userProfile.getFirstName());
			profile.setLastName(userProfile.getLastName());
			profile.setBio(userProfile.getBio());
			profile.setProfileImage(userProfile.getProfileImage());
			UserProfile user = repo.save(profile);
			return user;
		}
		else {
			return null;
		}
		
	}

	@Override
	public boolean deleteProfile(String username) {
		
		restTemplate.delete("http://IDEA-SERVICE:8083/ideas/deleteBy/" + username);
		
		Optional<UserProfile> optional = repo.getByUsername(username);
		
		if(optional.isPresent()) {
			repo.delete(optional.get());
			return true;
		}
		else {
			return false;
		}
		
	}
	
	@Override
	public boolean authenticateUser(Credentials credentials) {
        Optional<UserProfile> optionalUser = repo.getByUsername(credentials.getUsername());
        
        if (optionalUser.isPresent()) {
        	
        	UserProfile profile = optionalUser.get();
        	
        	if(profile.getPassword().equals(credentials.getPassword())) {
        		return true;
	        }
	        else {
	        	return false;
	        }
        	
        }
        else {
        	return false;
        }
        
    }
	
	
	@Override
    public String forgotPassword(String username) {
        Optional<UserProfile> userOptional = repo.getByUsername(username);
        
        if (userOptional.isPresent()) {
            String tempPassword = generateTemporaryPassword();
            UserProfile user = userOptional.get();
            user.setPassword(tempPassword);
            repo.save(user);
            return tempPassword;
        }
        else {
        	 return null;
        }
        
    }

    @Override
    public boolean changePassword(ChangePasswordDTO changePassword) {
        Optional<UserProfile> userOptional = repo.getByUsername(changePassword.getUsername());
        
        if (userOptional.isPresent()) {
            UserProfile user = userOptional.get();
            if (changePassword.getOldPassword().equals(user.getPassword())) {
                user.setPassword(changePassword.getNewPassword());
                repo.save(user);
                return true;
            }
            else {
            	return false;
            }
        }
        else {
       	 return false;
       }
    }

    private String generateTemporaryPassword() {
    	
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            tempPassword.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return tempPassword.toString();
    }

}
