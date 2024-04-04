package com.tms.configuration;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings({ "unused", "deprecation" })
@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class MyConfig {

	@Autowired
	customUserHandler customUserHandler;

	@Bean
	public UserDetailsService detailsService() {
		return new CustomUserDetailService();
	}

	// @Bean
	// public BCryptPasswordEncoder bCryptPasswordEncoder ()
	// {
	// return new BCryptPasswordEncoder();
	// }

	@SuppressWarnings("deprecation")
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

		authenticationProvider.setUserDetailsService(this.detailsService());
		authenticationProvider.setPasswordEncoder(this.passwordEncoder());
		// authenticationProvider.setPasswordEncoder(this.passwordEncoder());

		return authenticationProvider;
	}

	@SuppressWarnings("deprecation")
	@Bean
	public SecurityFilterChain chain(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeRequests()
				.requestMatchers(antMatcher("/student/**"))
				.hasRole("STUDENT")
				.requestMatchers(antMatcher("/teacher/**"))
				.hasRole("TEACHER")
				.requestMatchers(antMatcher("/admin/**")).access("hasRole('ADMIN')")
				.requestMatchers(antMatcher("/**"))
				.permitAll()
				.and()
				.formLogin()
				.loginPage("/slogin")
				.loginProcessingUrl("/slogin")
				// .defaultSuccessUrl("/teacher/index")
				.successHandler(customUserHandler)
				.and()
				.csrf()
				.disable();

		return http.build();

	}

}
