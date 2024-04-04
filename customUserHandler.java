package com.tms.configuration;

import java.io.IOException;
import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class customUserHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		Set<String> role = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		if (role.contains("ROLE_STUDENT")) {
			response.sendRedirect("/student/index");
		} else if (role.contains("ROLE_TEACHER")) {
			response.sendRedirect("/teacher/index");
		} else if (role.contains("ROLE_ADMIN")) {
			response.sendRedirect("/admin/index");
		}

		else {
			System.out.println("invalid Url.......");
		}
	}

}
