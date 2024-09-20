package com.dbdevdeep.place.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.place.domain.PlaceDto;
import com.dbdevdeep.place.service.PlaceService;

@Controller
public class PlaceApiController {

	private final PlaceService placeService;
	private final FileService fileService;
	
	@Autowired
	public PlaceApiController(PlaceService placeService, FileService fileService) {
		this.placeService = placeService;
		this.fileService = fileService;
		
	}
	
	// 삭제하기
	@ResponseBody
	@DeleteMapping("/place/{place_no}")
	public Map<String, String> deletePlace(@PathVariable("place_no") Long place_no){
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "장소 삭제중 오류가 발생하였습니다.");
		
		if(placeService.deletePlace(place_no) > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "장소 삭제에 성공했습니다.");
		}
		return resultMap;
	}
	
	
	// 수정하기
	@ResponseBody
	@PostMapping("/place/update/{place_no}")
	public Map<String, String> updatePlace(PlaceDto dto, @RequestParam(name ="file", required=false) MultipartFile file){
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "장소 수정중 오류가 발생하였습니다.");
		
		 try {
	            // 장소 정보 수정 처리
	            if (placeService.updatePlace(dto, file) > 0) {
	                resultMap.put("res_code", "200");
	                resultMap.put("res_msg", "장소가 성공적으로 수정되었습니다.");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            resultMap.put("res_msg", "서버 오류로 인해 장소 수정에 실패하였습니다.");
	        }

	        return resultMap;
	}
	// 등록글
	@ResponseBody
	@PostMapping("/place")
	public Map<String, String> createPlace(@ModelAttribute PlaceDto dto, @RequestParam("file") MultipartFile file) {
	    Map<String, String> resultMap = new HashMap<>();
	    
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "장소 등록 중 오류가 발생하였습니다.");
	    
	    try {
	        // 장소 정보 생성 (파일 처리 포함)
	        if (placeService.createPlace(dto, file) > 0) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "장소가 성공적으로 등록되었습니다.");
	        } else {
	            resultMap.put("res_msg", "장소 등록에 실패하였습니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_msg", "서버 오류로 인해 장소 등록에 실패하였습니다.");
	    }

	    return resultMap;
	}

}