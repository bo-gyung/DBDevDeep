package com.dbdevdeep.security.service;

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
import com.dbdevdeep.employee.repository.EmployeeRepository;
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
		
		if(employee != null) {
			EmployeeDto dto = new EmployeeDto().toDto(employee);
			
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority(employee.getDeptCode()));
			
			dto.setAuthorities(authorities);
			
			return new SecurityUser(dto);
		} else {
			throw new UsernameNotFoundException(username);
		}
	}
	
}
