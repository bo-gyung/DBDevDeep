package com.dbdevdeep.place.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.place.domain.Item;
import com.dbdevdeep.place.service.ItemService;
import com.dbdevdeep.place.service.PlaceScheduleService;
import com.dbdevdeep.place.vo.PlaceItemScheduleVo;

@Controller
public class PlaceScheduleApiController {
	
	private final PlaceScheduleService placeScheduleService;
	private final ItemService itemService;  // ItemService 주입

	@Autowired
	public PlaceScheduleApiController(PlaceScheduleService placeScheduleService, ItemService itemService) {
		this.placeScheduleService = placeScheduleService;
		this.itemService = itemService;
	}
	
	
	
	// 등록
	@ResponseBody
	@PostMapping("/place_schedule")
	public Map<String,String> createPlaceSchedule(@RequestBody PlaceItemScheduleVo vo){
		Map<String, String> resultMap = new HashMap<String, String>();
	    
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "일정 등록 중 오류가 발생하였습니다.");
	    
	    if(placeScheduleService.createPlaceSchedule(vo) != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정이 성공적으로 등록되었습니다.");
		} else {
			resultMap.put("res_msg", "일정 등록 중 예외가 발생하였습니다.");
		}
		
		return resultMap;
	}
	
}
