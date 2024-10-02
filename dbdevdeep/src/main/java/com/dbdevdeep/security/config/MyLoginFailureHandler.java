package com.dbdevdeep.security.config;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.repository.EmployeeRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

		String empId = request.getParameter("emp_id");
		Employee e = employeeRepository.findByempId(empId);
		EmployeeDto dto = new EmployeeDto().toDto(e);

		int failureCount = e.getAccountStatus(); // 틀린 횟수 
		LocalDateTime lockoutTime = e.getAccountTime(); // 5회 틀렸을 때 시간

		LocalDateTime now = LocalDateTime.now(); // 현재 시간
		// status 1 증가
		dto.setAccount_status(++failureCount); 
		Employee employee = dto.toEntityWithJoin(e.getDepartment(), e.getJob());
		
		// locoutTime 존재 && 틀렸을 때 시간보다 20초 이전
		if (lockoutTime != null && now.isBefore(lockoutTime.plusSeconds(1200))) {
			request.getSession().setAttribute("loginErrorNum", 5);
			
			response.sendRedirect("/login");
			return;
		}

		// locoutTime 존재 => 5회 이상 틀림 && 20초 이후 => status 및 time 초기화
		if (lockoutTime != null && failureCount >= 5) {
			dto.setAccount_status(0);
			dto.setAccount_time(null);
			employeeRepository.save(dto.toEntityWithJoin(e.getDepartment(), e.getJob()));
		}
		
		// 5회 이상 틀림 && lockoutTime 존재 X
		if (failureCount >= 5) { 
			dto.setAccount_time(now);
			employeeRepository.save(dto.toEntityWithJoin(e.getDepartment(), e.getJob()));
			request.getSession().setAttribute("loginErrorNum", failureCount);
		 } else { 
			 request.getSession().setAttribute("loginErrorNum", failureCount);
			 employeeRepository.save(employee);
			 response.sendRedirect("/login"); 
			 return;
		}

		// 리다이렉트
		response.sendRedirect("/login");
	}
}