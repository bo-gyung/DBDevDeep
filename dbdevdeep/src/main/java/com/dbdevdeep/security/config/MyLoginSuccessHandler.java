package com.dbdevdeep.security.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.mybatis.mapper.EmployeeVoMapper;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.vo.EmployeeVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private final EmployeeRepository employeeRepository;
	private final EmployeeVoMapper employeeVoMapper;
	
	private MyLoginSuccessHandler(EmployeeRepository employeeRepository,
			EmployeeVoMapper employeeVoMapper) {
		this.employeeRepository = employeeRepository;
		this.employeeVoMapper =employeeVoMapper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String id = request.getParameter("emp_id");
		
		Employee employee = employeeRepository.findByempId(id);
		
		EmployeeDto dto = new EmployeeDto().toDto(employee);
		
		dto.setAccount_status(0);
		
		EmployeeVo empVo = new EmployeeVo(dto.getEmp_id(), dto.getEmp_pw(), dto.getGov_id(), dto.getEmp_name(),
				dto.getEmp_rrn(), dto.getEmp_phone(), dto.getOri_pic_name(), dto.getNew_pic_name(),
				dto.getEmp_post_code(), dto.getEmp_addr(), dto.getEmp_detail_addr(), dto.getDept_code(),
				dto.getJob_code(), dto.getEmp_internal_phone(), dto.getVacation_hour(), dto.getHire_date(),
				dto.getEnd_date(), dto.getEnt_status(), "Y", dto.getAccount_status(), dto.getChat_status_msg(),
				dto.getAccount_time());

		// 로그인 성공 시 login_yn값 & account_status 초기화 db에 반영
		employeeVoMapper.updateLoginYn(empVo);

		response.sendRedirect("/");

	}
}
