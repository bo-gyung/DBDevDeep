package com.dbdevdeep.security.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.mybatis.mapper.EmployeeVoMapper;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.vo.EmployeeVo;
import com.dbdevdeep.security.vo.SecurityUser;

@Service
public class SecurityService implements UserDetailsService {

	private final EmployeeRepository employeeRepository;

	@Autowired
	public SecurityService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee employee = employeeRepository.findByempId(username);

		if (employee != null) {
			if ("Y".equals(employee.getEntStatus())
					& LocalDateTime.now().isAfter(employee.getAccountTime().plusSeconds(1200))) { // 재직
				return LoginData(employee);
			} else {
				throw new UsernameNotFoundException(username);
			}
		} else {
			throw new UsernameNotFoundException(username);
		}
	}

	public SecurityUser LoginData(Employee employee) {
		Employee e = employeeRepository.findByempId(employee.getEmpId());
		EmployeeDto d = new EmployeeDto().toDto(e);

		// authorities 설정
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		// 부서 기준 권한 설정
		authorities.add(new SimpleGrantedAuthority(employee.getDepartment().getDeptCode()));

		// 직위 기준 권한 설정
		authorities.add(new SimpleGrantedAuthority(employee.getJob().getJobCode()));

		// 재직 기준 권한 설정
		authorities.add(new SimpleGrantedAuthority(employee.getEntStatus()));

		d.setAuthorities(authorities);

		return new SecurityUser(d);

	}

}
