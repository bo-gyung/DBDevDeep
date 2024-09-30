package com.dbdevdeep.security.config;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.dbdevdeep.employee.repository.EmployeeRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class MyLoginFailureHandler implements AuthenticationFailureHandler {

	private final EmployeeRepository employeeRepository;

	@Autowired
	public MyLoginFailureHandler(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		HttpSession session = request.getSession();

		// 로그인 실패 횟수 가져오기
		Integer failureCount = (Integer) session.getAttribute("failureCount");
		Instant lockoutTime = (Instant) session.getAttribute("lockoutTime");
		
		System.out.println("check1");
		System.out.println("failureCount: " + failureCount);
		System.out.println("lockoutTime: " + lockoutTime);

		// Check if currently locked out
		if (lockoutTime != null && Instant.now().isBefore(lockoutTime.plusSeconds(1200))) {
			// Still locked out; redirect with a message
			response.sendRedirect("/login?error=locked");
			return;
		}
		
		System.out.println("check2");

		// Reset lockout time if it has expired
		if (lockoutTime != null) {
			session.removeAttribute("lockoutTime");
		}
		
		System.out.println("check3");

		// Update failure count
		if (failureCount == null) {
			failureCount = 1; // First failure
		} else {
			failureCount++; // Increment failure count
		}
		
		session.setAttribute("failureCount", failureCount);
		
		System.out.println("check4");
		
		// Check if failure count has reached 5
		if (failureCount >= 5) {
			// Lock out the user for 20 minutes
			session.setAttribute("lockoutTime", Instant.now());
			response.sendRedirect("/login?error=locked");
		} else {
			// Redirect to login page with a failure message
			response.sendRedirect("/login?error=failed");
		}
	}
}
