package com.dbdevdeep.alert.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.alert.domain.AlertDto;
import com.dbdevdeep.alert.service.AlertService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AlertViewController {

	private final AlertService alertService;
	
	@ResponseBody
	@GetMapping("/alert")
	public Map<String, Object> showAlert() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "알람정보를 가져오던 중 오류가 발생하였습니다.");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emp_id = authentication.getName();
        
        List<AlertDto> alertList = alertService.selectAlertByEmpId(emp_id);
        
        if(alertList.size() > 0) {
        	resultMap.put("res_code", "200");
    		resultMap.put("res_msg", alertList);
        }
        
        return resultMap;
	}
}
