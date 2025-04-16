package com.shareideas.user.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shareideas.user.model.ChangePasswordDTO;
import com.shareideas.user.model.Credentials;
import com.shareideas.user.model.UserProfile;
import com.shareideas.user.model.UserProfileDTO;
import com.shareideas.user.service.UserProfileService;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserProfileController {
	
	@Autowired
	UserProfileService userProfileService;
	
//	@PostMapping("/add")
//	public ResponseEntity<?> addUser(@RequestBody UserProfile userProfile){
//		
//		UserProfile user = userProfileService.saveUser(userProfile);
//		
//		if(user!=null) {
//			return new ResponseEntity<UserProfile>(user,HttpStatus.OK);
//		}
//		else {
//			return new ResponseEntity<String>("User Cannot Be Stored",HttpStatus.CONFLICT);
//		}
//		
//	}
	
	@PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody UserProfileDTO userProfileDTO) {
        try {
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(userProfileDTO.getUsername());
            userProfile.setPassword(userProfileDTO.getPassword());
            userProfile.setEmail(userProfileDTO.getEmail());
            userProfile.setFirstName(userProfileDTO.getFirstName());
            userProfile.setLastName(userProfileDTO.getLastName());
            userProfile.setBio(userProfileDTO.getBio());

            // Set default profile image if none provided
            if (userProfileDTO.getProfileImage() == null || userProfileDTO.getProfileImage().isEmpty()) {
                try {
                    ClassPathResource defaultImage = new ClassPathResource("static/defaultprofile.png");
                    byte[] defaultImageBytes = StreamUtils.copyToByteArray(defaultImage.getInputStream());
                    userProfile.setProfileImage(defaultImageBytes);
                } catch (IOException e) {
                    return new ResponseEntity<String>("Error loading default image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                try {
                    userProfile.setProfileImage(Base64.getDecoder().decode(userProfileDTO.getProfileImage()));
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<String>("Invalid profile image base64 data", HttpStatus.BAD_REQUEST);
                }
            }

            UserProfile user = userProfileService.saveUser(userProfile);
            if (user != null) {
                return new ResponseEntity<UserProfile>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("User Cannot Be Stored", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>("Error adding user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GetMapping("/get/{username}")
	public ResponseEntity<?> getUserDetails(@PathVariable String username){
		
		UserProfile user = userProfileService.getUser(username);
		
		if(user!=null) {
			return new ResponseEntity<UserProfile>(user,HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>("User Isn't Present",HttpStatus.NOT_FOUND);
		}
		
	}
	
	@GetMapping("/getall")
	public ResponseEntity<?> getAllUserDetails(){
		
		List<UserProfile> list = userProfileService.getAll();
		
		if(list!=null) {
			return new ResponseEntity<List<UserProfile>>(list,HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>("Users are not Present",HttpStatus.NOT_FOUND);
		}
		
	}
	
//	@PutMapping("/update")
//	public ResponseEntity<?> updateUserDetails(@RequestBody UserProfile userProfile){
//		
//		UserProfile user = userProfileService.updateProfile(userProfile);
//		
//		
//		if(user!=null) {
//			return new ResponseEntity<UserProfile>(user,HttpStatus.OK);
//		}
//		else {
//			return new ResponseEntity<String>("User Cannot Be There",HttpStatus.CONFLICT);
//		}
//		
//	}
	
	
	@PutMapping("/update")
    public ResponseEntity<?> updateUserDetails(@RequestBody UserProfileDTO userProfileDTO) {
        try {
            UserProfile existingUser = userProfileService.getUser(userProfileDTO.getUsername());
            if (existingUser == null) {
                return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
            }

            // Update fields if provided
            if (userProfileDTO.getPassword() != null) existingUser.setPassword(userProfileDTO.getPassword());
            if (userProfileDTO.getEmail() != null) existingUser.setEmail(userProfileDTO.getEmail());
            if (userProfileDTO.getFirstName() != null) existingUser.setFirstName(userProfileDTO.getFirstName());
            if (userProfileDTO.getLastName() != null) existingUser.setLastName(userProfileDTO.getLastName());
            if (userProfileDTO.getBio() != null) existingUser.setBio(userProfileDTO.getBio());
            if (userProfileDTO.getProfileImage() != null && !userProfileDTO.getProfileImage().isEmpty()) {
                try {
                    existingUser.setProfileImage(Base64.getDecoder().decode(userProfileDTO.getProfileImage()));
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<String>("Invalid profile image base64 data", HttpStatus.BAD_REQUEST);
                }
            }

            UserProfile user = userProfileService.updateProfile(existingUser);
            if (user != null) {
                return new ResponseEntity<UserProfile>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("User Cannot Be Updated", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>("Error updating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@DeleteMapping("/delete/{username}")
	public ResponseEntity<?> deleteUserDetails(@PathVariable String username){
		
		boolean profile = userProfileService.deleteProfile(username);
		
		if(profile) {
			return new ResponseEntity<String>("User Is Deleted",HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>("User Isn't There",HttpStatus.CONFLICT);
		}
		
	}
	
	@PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestBody Credentials credentials) {
        boolean isAuthenticated = userProfileService.authenticateUser(credentials);

        if (isAuthenticated) {
        	return new ResponseEntity<String>("User Authenticate Successfully",HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Invalid username or password",HttpStatus.CONFLICT);
        }
    }
	
	
	@PostMapping("/forgetpassword/{username}")
    public ResponseEntity<String> forgotPassword(@PathVariable String username) {
        String tempPassword = userProfileService.forgotPassword(username);
        
        if (tempPassword == null) {
        	return new ResponseEntity<String>("Username Is Invalid",HttpStatus.CONFLICT);
        }
        else {
        	return new ResponseEntity<String>(tempPassword,HttpStatus.OK);
        }
        
    }
	

    @PostMapping("/changepassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        boolean isChanged = userProfileService.changePassword(changePasswordDTO);
        
        if (isChanged) {
        	return new ResponseEntity<String>("Password Successfully Changed",HttpStatus.OK);
        } else {
        	return new ResponseEntity<String>("Invalid username or oldpassword",HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/profile-image/{username}")
    public ResponseEntity<?> getProfileImage(@PathVariable String username) {
        UserProfile user = userProfileService.getUser(username);
        if (user != null && user.getProfileImage() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<byte[]>(user.getProfileImage(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("User or Profile Image Not Found", HttpStatus.NOT_FOUND);
        }
    }

}
