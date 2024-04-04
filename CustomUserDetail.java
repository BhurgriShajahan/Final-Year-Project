package com.tms.configuration;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tms.entities.User;

public class CustomUserDetail implements UserDetails {

	User user;

	public CustomUserDetail(User user) {
		super();
		this.user = user;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {

		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

		return List.of(authority);
	}

	public String getPassword() {
		return user.getPassword();
	}

	public String getUsername() {
		return user.getEmail();
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

}
