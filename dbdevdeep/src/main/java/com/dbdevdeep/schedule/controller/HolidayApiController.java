package com.dbdevdeep.schedule.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.schedule.domain.HolidayDto;
import com.dbdevdeep.schedule.service.HolidayService;

@Controller
public class HolidayApiController {
	
	private final HolidayService holidayService;
	
	@Autowired
	public HolidayApiController(HolidayService holidayService) {
		this.holidayService = holidayService;
	}
	
	@ResponseBody
	@PostMapping("/holiday")
	public Map<String,String> createHoliday(@RequestBody HolidayDto dto){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "휴일 등록 중 오류가 발생하였습니다.");
		
		if(holidayService.createHoliday(dto) != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "휴일이 성공적으로 등록되었습니다.");
		} else {
			resultMap.put("res_msg", "휴일 등록 중 예외가 발생하였습니다.");
		}
		
		return resultMap;
	}
}
