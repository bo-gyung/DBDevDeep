package com.dbdevdeep.alert.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.alert.domain.Alert;
import com.dbdevdeep.alert.service.AlertService;
import com.dbdevdeep.approve.service.ApproveService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AlertApiController {

	private final AlertService alertService;
	private final ApproveService approveService;
	
	@ResponseBody
	@PutMapping("/alert/{alarm_no}")
	public Map<String, Object> showDeleteAlert(@PathVariable("alarm_no") Long alarm_no) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("res_code", "404");

		Alert alert = alertService.showDeleteAlert(alarm_no);
		
		if(alert != null) {
			resultMap.put("res_code", "200");
		}

		return resultMap;
	}
}
