package com.dbdevdeep.security.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class MyLoginFailureHandler implements AuthenticationFailureHandler{
	
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

        // 로그인 실패 횟수 초기화 또는 증가
        if (failureCount == null) {
            failureCount = 1; // 첫 실패
        } else {
            failureCount++; // 실패 횟수 증가
        }
		
		System.out.println("emp_id: " + request.getParameter("emp_id"));
		
		Employee e = employeeRepository.findByempId(request.getParameter("emp_id"));
		System.out.println(e.getAccountStatus());
		
		response.sendRedirect("/login");
	}
}
