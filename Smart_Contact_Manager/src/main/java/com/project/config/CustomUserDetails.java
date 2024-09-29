package com.project.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.Entities.User;

public class CustomUserDetails implements UserDetails{

	private User user;
	
	public CustomUserDetails(User user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
	// here role is a common way to implement access control . we are using it in authorirty so that in cinfiguration we can allow access to links based on role of  either admin or user
	SimpleGrantedAuthority simpleGrantedAuthority =	new SimpleGrantedAuthority(user.getRole());
		
		
		return List.of(simpleGrantedAuthority);
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}


}
