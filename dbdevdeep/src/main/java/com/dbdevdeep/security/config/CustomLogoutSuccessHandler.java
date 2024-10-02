package com.dbdevdeep.security.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.mybatis.mapper.EmployeeVoMapper;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.vo.EmployeeVo;
import com.dbdevdeep.security.vo.SecurityUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	private final EmployeeRepository employeeRepository;
	private final EmployeeVoMapper employeeVoMapper;

	@Autowired
	public CustomLogoutSuccessHandler(EmployeeRepository employeeRepository,
			EmployeeVoMapper employeeVoMapper) {
		this.employeeRepository = employeeRepository;
		this.employeeVoMapper = employeeVoMapper;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
			SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
			String username = securityUser.getUsername();
			Employee employee = employeeRepository.findByempId(username);

			if (employee != null) {
				EmployeeDto dto = new EmployeeDto().toDto(employee);
				
				EmployeeVo empVo = new EmployeeVo(dto.getEmp_id(), dto.getEmp_pw(), 
						dto.getGov_id(), dto.getEmp_name(), dto.getEmp_rrn(), dto.getEmp_phone(),
						dto.getOri_pic_name(), dto.getNew_pic_name(), dto.getEmp_post_code(), dto.getEmp_addr(),
						dto.getEmp_detail_addr(), dto.getDept_code(), dto.getJob_code(), 
						dto.getEmp_internal_phone(), dto.getVacation_hour(), dto.getHire_date(),
						dto.getEnd_date(), dto.getEnt_status(), "N",
						dto.getAccount_status(), dto.getChat_status_msg(), dto.getAccount_time());
				
				// login_yn값 db에 반영
				int resultInt = employeeVoMapper.updateLoginYn(empVo);
				
				if(resultInt > 0) {
					response.sendRedirect("/login");
				}
			}

			
		}

	}

}
