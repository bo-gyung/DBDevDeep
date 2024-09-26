package com.dbdevdeep.security.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyLoginFailureHandler implements AuthenticationFailureHandler {

	private final String empId = "emp_id";
	private final String empPw = "emp_pw";

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String username = request.getParameter(empId);
		String userpw = request.getParameter(empPw);
		
		request.setAttribute(empId, username);
		request.setAttribute(empPw, userpw);
		
		response.sendRedirect("/login");
	}
}
