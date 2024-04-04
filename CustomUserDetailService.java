package com.tms.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tms.entities.User;
import com.tms.repo.UserRepo;

public class CustomUserDetailService implements UserDetailsService {
	@Autowired
	UserRepo repo;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.getUserByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("Sorry...Username not found");
		}

		return new CustomUserDetail(user);
	}

}
