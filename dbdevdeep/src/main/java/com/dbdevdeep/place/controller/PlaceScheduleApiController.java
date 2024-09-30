package com.dbdevdeep.place.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.place.domain.PlaceItemSchedule;
import com.dbdevdeep.place.repository.ItemRepository;
import com.dbdevdeep.place.service.ItemService;
import com.dbdevdeep.place.service.PlaceScheduleService;
import com.dbdevdeep.place.vo.PlaceItemScheduleVo;

@Controller
public class PlaceScheduleApiController {
	
	private final PlaceScheduleService placeScheduleService;
	private final ItemService itemService;  // ItemService 주입
	private final ItemRepository itemRepository; // ItemRepository 주입
	@Autowired
	public PlaceScheduleApiController(PlaceScheduleService placeScheduleService, ItemService itemService,
			ItemRepository itemRepository) {
		this.placeScheduleService = placeScheduleService;
		this.itemService = itemService;
		this.itemRepository = itemRepository;
	}
	
	
	// 삭제
	@ResponseBody
	@DeleteMapping("/place_schedule/delete/{place_schedule_no}")
	public Map<String, String> deletePlaceSchedule(@PathVariable("place_schedule_no") Long place_schedule_no){
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "일정 삭제중 오류가 발생하였습니다.");
		
		if(placeScheduleService.deletePlaceSchedule(place_schedule_no) > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정 삭제에 성공했습니다.");
		}
		return resultMap;
	}
	
	
	// 등록
	@ResponseBody
    @PostMapping("/place_schedule")
	public Map<String,String> createPlaceSchedule(@ModelAttribute PlaceItemScheduleVo vo){
		Map<String, String> resultMap = new HashMap<String, String>();
		 resultMap.put("res_code", "404");
		    resultMap.put("res_msg", "일정 등록 중 오류가 발생하였습니다.");

		    try {
		        PlaceItemSchedule result = placeScheduleService.createPlaceSchedule(vo);
		        if (result != null) {
		            resultMap.put("res_code", "200");
		            resultMap.put("res_msg", "일정이 성공적으로 등록되었습니다.");
		        } else {
		            resultMap.put("res_msg", "일정이 겹쳐서 등록할 수 없습니다."); // 일정이 겹칠 때의 메시지
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        resultMap.put("res_msg", "일정 등록 중 예외가 발생하였습니다.");
		    }

		    return resultMap;
	}
}
