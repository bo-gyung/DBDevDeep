package com.dbdevdeep.alert.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dbdevdeep.alert.domain.Alert;
import com.dbdevdeep.alert.domain.AlertDto;
import com.dbdevdeep.alert.repository.AlertRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AlertService {

	private final AlertRepository alertRepository;
	
	public List<AlertDto> selectAlertByEmpId(String emp_id) {
		List<AlertDto> alertDtoList = new ArrayList<>();
		List<Alert> alertList = alertRepository.findByempId(emp_id);
		
		for(Alert alert : alertList) {
			AlertDto dto = new AlertDto().toDto(alert);
			
			alertDtoList.add(dto);
		}
		
		return alertDtoList;
	}
}
